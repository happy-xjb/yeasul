# yeasul
轻量级服务注册与发现中心，具备心跳检测功能。

## 数据库配置

依赖于Mysql数据库，版本5.7。创建名为consul_schema的数据库，建表语句在consul-server-8500的consul_schema.sql文件中。项目默认连接云服务器的数据库。

## application.yml配置

> 请使用gson作为默认的json解析器，而不是默认的jackson或其他。为了兼容consul-api（使用gson）。

> 如果注册带有检查的服务，参考dept-provider-8001的application.yml。