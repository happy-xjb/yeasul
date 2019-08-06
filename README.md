# yeasul
亿联定制版服务注册与发现中心，具备心跳检测功能，由于接口参考了consul，所以取名为yeasul。

# 项目架构

使用Java语言开发，依赖于MySQL 8.0数据库，基于Spring Boot 2.1.6开发。Yeasul会将服务信息持久化到数据库，并在每次重新启动Yeasul时，会重新开启对服务的健康检查。在集群情况下，集群中的每个Yeasul连接的是同一个数据库，即一个集群将会共同连接一个数据库，从而保证了数据的一致性。这与consul的每个consul拥有各自独立的数据持久化存储不同。当然也可以对MySQL数据库作主从配置，防止数据库宕机。

## 数据库模型

注册进Yeasul的所有信息将会被持久化进数据库，如下为数据库的模型图。

![](./README_images/MYSQL_ENTITIES.bmp)

### node表

> 用于记录集群中的节点信息

node_id:节点ID，在启动一个节点时，会分配一个UUID

name:节点名称，在启动前application.yml中配置，名称不允许有重复

address:节点IP地址

datacenter:数据中心名称

### check表

node:负责此检查的节点名

check_id:检查的id，默认格式为service:服务实例ID

name:检查的名称，默认格式为Service '服务名称' check

status:三个值，passing,warning,failing,分别代表通过，警告和失败，实际上只用到了passing和failing。

notes:检查的备注

output:每次执行完一次检查后，将会更新output的值，比如HTTP GET http://127.0.0.1:8500 200 OK Output:Apollo

service_id:检查的服务ID

service:检查的服务的名称

### check_info表

> 此表用于持久化检查的信息，用于在每次yeasul重启时，能够继续执行原有的检查

check_id:检查的id，默认格式为service:服务实例ID

kind:检查的方式，http,tcp

url:检查的url，http的形式如http://127.0.0.1:8500/index，tcp的形式如127.0.0.1:8500

interval:定义间隔多久执行一次检查，例如5s表示每隔5秒执行一次检查，支持的单位有ns,us,ms,s,m,h(纳秒，微妙，毫秒，秒，分，时)

timeout:定义检查的超时时间，格式与interval相同。若超出超时时间没有得到正确的相应，则会将**check表**中的对应的check_id的记录的status置为failing。反之为passing。

node:负责执行此检查的节点名称

### service_name表

> 此表用于记录注册在数据库中所有的服务名称

service:服务名称

### service_instance表

> 用于记录注册的服务实例信息

service_instance_id:服务实例ID

service:服务名称

address:服务的IP地址

port:服务的端口号

### service_tag

> 用于记录服务实例的标签信息。注意：如果一个服务实例有两个标签A和B，那么将会在此表内产生两条记录。并不是将A和B放在同一记录内

service:服务名称

value:标签值

service_id:服务实例ID

### register_info

> 用于记录哪个服务是注册在哪个节点上的，以及位于哪个数据中心

service_instance_id:服务实例ID

node_id:节点ID，服务注册在此节点上

datacenter:数据中心名称。服务位于此数据中心

service:服务名称

### key_value表

> 存储key/value

datacenter:数据中心名称，表示此kv存储于此数据中心

key:key值，xxx/xxx表示值，xxx/xxx/表示目录

value:value值

# 上手指南

帮助你在本地机器上安装和运行该项目，进行开发和测试。关于如何将该项目部署到在线环境，请参考部署小节。

## 环境要求

- JDK 1.8.0_144

- MySQL 8.0

## 环境配置

### 数据库配置

创建名为consul_schema的数据库，并使用consul-server-8500模块下的consul_schema.sql创建表。

### application.yml配置

```yaml
server:
  port: 8500	#设置服务端端口号
spring:
  datasource:
    username: root	#数据库账户
    password: 123456	#数据库密码
    url: jdbc:mysql://62.234.44.124:3306/consul_schema	#数据库url
    driver-class-name: com.mysql.cj.jdbc.Driver	
  http:
    converters:
      preferred-json-mapper: gson #必须使用gson，否则健康检查会出错，必须！
mybatis:
  mapper-locations: classpath:/mappers/*.xml
  type-aliases-package: com.yealink.entities
  configuration:
    map-underscore-to-camel-case: true
consul:
  debug-config:
    bind-address: 10.83.2.93	#设置本服务端IP地址，必要
  config:
    node-name: myNode1	#设置节点名，必要，且节点名唯一
    datacenter: CN_DC1 #设置数据中心名，要加入同一集群，需设置相同的数据中心名，必要

```

# 快速开始

经过上面的环境搭建，并可以运行yeasul了。

使用浏览器访问,127.0.0.1:8500，如果正常进入如下页面，就表示你已经可以开始使用yeasul了。

![](./README_images/index.bmp)

## 如何注册服务？

有两个注册方式，如果是Spring Boot项目，可以通过配置application.yml的方式进行注册。也可以通过**PUT**请求向/v1/agent/service/register发送带有服务相关信息的JSON数据进行注册。

### Spring Boot服务注册

需要在pom中引入spring-cloud-starter-consul-discovery和spring-boot-starter-actuator。

并在application.yml中进行如下配置：

```yaml
spring:
  application:
    name: dept-provider-8001	#服务名
  cloud:
    consul:
      host: 10.83.2.93	#yeasul的IP地址
      port: 8500	#yeasul的端口号
      discovery:
        service-name: dept-provider #注册进yeasul的服务名称
        ip-address: 10.83.2.93 #此服务的ip地址
        tags: ["version=1.0","author=Happy-xjb"] #服务标签
        prefer-ip-address: true	#以IP地址的形式注册，而不是主机名
        register-health-check: true	#如果注册服务时同时注册HTTP健康检查，则置true，并填写下方所有参数。如果不需要注册健康检查，则不要填写下方所有参数。
        health-check-path: /actuator/health #健康检查的URL
        health-check-interval: 5s #每隔5s执行一次检查
        health-check-timeout: 5s	#超时时间
```

如果需要自定义健康检查地址，请暴露自定义的接口，接口返回类型为org.springframework.boot.actuate.health.Health。

### 接口方式注册

**请求URL**

> PUT	/v1/agent/service/register

**请求示例**

```json
{
"ID": "redis1",	//服务实例ID
"Name": "redis",	//服务名称
"Tags": [	//服务标签
 "primary",
 "v1"
],
"Address": "127.0.0.1",	//服务IP地址
"Port": 8000,	//服务的端口号
"Check": {	//如果注册时需要注册健康检查，按如下填写，HTTP和TCP字段二选一，Interval和Timeout必要。
 "HTTP": "http://localhost:5000/health",
 "TCP" : "127.0.0.1:8500",
 "Interval": "10s",
 "Timeout": "15s"
}
}
```

通过以上两种方式注册后，如果在页面或者数据库中成功显示了新注册的服务，那么就表示服务注册成功了!

## 如何注销服务？

有两种方式，一是在页面中的NODES模块下找到相应的服务进行注销，非常简单。二是通过HTTP接口进行注销。下面介绍第二种方式。

**请求URL**

> PUT	/v1/agent/service/deregister/{serviceId}

## 查看当前节点信息

Self接口

**请求URL**

> GET	/v1/agent/self

## 查看已注册的服务

一是在页面中查看，二是GET请求访问/v1/agent/services。

## 注册健康检查

你可以注册一个单独的健康检查，通过HTTP接口。

**请求URL**

> PUT	/v1/agent/check/register

**请求示例**

```json
{
"ID": "mem",	//checkID
"Name": "Memory utilization",	//检查的名称
"Notes": "Ensure we don't oversubscribe memory",	//检查的备注
"ServiceID": "redis1",	//你也可以不填写服务ID，单单启动一个健康检查
//HTTP和TCP选一种
"HTTP": "https://example.com",
"TCP": "example.com:22",
"Interval": "10s",
"Timeout": "15s"
}
```

## 查看所有服务的检查

可以在页面中查看，也可以通过GET请求访问/v1/agent/checks

## 查看集群中某个服务的健康状态

可以在页面中查看，可以通过GET请求访问/v1/health/service/{service}

service:指服务的名称

# 集群配置

下面演示如何搭建两台服务器的集群。

假设有两台服务器A和B，如果要启动两台服务器的集群，请在A和B的application.yml严格按照如下要求配置。

**A的application.yml**

```yml
server:
  port: 8500
spring:
  datasource:
    username: root
    password: 123456
    url: jdbc:mysql://62.234.44.124:3306/consul_schema	#将A和B连接至相同数据库
    driver-class-name: com.mysql.cj.jdbc.Driver
  http:
    converters:
      preferred-json-mapper: gson 
mybatis:
  mapper-locations: classpath:/mappers/*.xml
  type-aliases-package: com.yealink.entities
  configuration:
    map-underscore-to-camel-case: true
consul:
  debug-config:
    bind-address: 10.83.2.93	#配置好A的IP地址
  config:
    node-name: myNode1	#A的节点名，保持节点名在数据库中唯一！
    datacenter: CN_DC1	#A要加入的集群，配置和B一致

```

**B的application.yml**

```yaml
server:
  port: 8500
spring:
  datasource:
    username: root
    password: 123456
    url: jdbc:mysql://62.234.44.124:3306/consul_schema	#和A连接至相同数据库
    driver-class-name: com.mysql.cj.jdbc.Driver
  http:
    converters:
      preferred-json-mapper: gson 
mybatis:
  mapper-locations: classpath:/mappers/*.xml
  type-aliases-package: com.yealink.entities
  configuration:
    map-underscore-to-camel-case: true
consul:
  debug-config:
    bind-address: 192.168.160.128 #配置好B的IP地址
  config:
    node-name: myNode2	#B的节点名，保持节点名在数据库中唯一！
    datacenter: CN_DC1	#B要加入的集群，配置和B一致

```

