# yeasul
轻量级服务注册与发现中心，具备心跳检测功能。

## 数据库配置

依赖于Mysql数据库，版本8.0。创建名为consul_schema的数据库，建表语句在consul-server-8500的consul_schema.sql文件中。

## application.yml配置

如果注册带有检查的服务，参考dept-provider-8001的application.yml。

