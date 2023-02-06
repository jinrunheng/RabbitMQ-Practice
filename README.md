# RabbitMQ-Practice

## 项目描述
该项目是一个基于 RabbitMQ 为核心的外卖订单后端微服务系统。
...

## RabbitMQ 快速安装与启动
            
### Mac 使用 Homebrew 安装 RabbitMQ
1. `brew update`
2. `brew install rabbitmq`；mac 默认的下载路径为：`/usr/local/Cellar/rabbitmq/版本号/`

### 启动 RabbitMQ

首先我们要配置 `.zshrc` 文件，vi 进入编辑模式后，在文件中添加 `export PATH=/usr/local/Cellar/rabbitmq/3.11.2/sbin/:$PATH`，注意路径要填写自己的下载路径，填写完毕后执行 `source ~/.zshrc`。

方式一：

- 使用命令：`brew services start rabbitmq` 来启动 RabbitMQ
- 对应的关闭方式为：`brew services stop rabbitmq`

方式二：

- 使用命令：`rabbitmq-server` 来启动 RabbitMQ
- 使用 `Ctrl + C` 可以直接关闭服务

方式三：

- 后台模式启动，使用命令：`rabbitmq-server -detached `
- 关闭服务使用命令：`rabbitmqctl stop`

### RabbitMQ 网页端管理控制台

- 启动命令：`rabbitmq-plugins enable rabbitmq_management`
- 浏览器打开：`127.0.0.1:15672`
- 默认用户名/密码：`guest/guest`

建议初始化用户名和密码,进入到 sbin 目录下，执行命令：
```bash
rabbitmqctl add_user admin 123456 #用户名admin，密码123456
rabbitmqctl set_user_tags admin administrator #设置用户最高操作权限
```
这样我们便设置了用户名和登陆密码为：`admin/123456`

登陆网页端控制台后，我们也可以自己去添加用户以及设置其权限

### RabbitMQ 常用命令行

#### 状态查看

- 查看状态:`rabbitmqctl status`
- 查看绑定：`rabbitmqctl list_bindings`
- 查看 channel：`rabbitmqctl list_channels`
- 查看 connection：`rabbitmqctl list_connections`
- 查看消费者：`rabbitmqctl list_consumers`
- 查看交换机：`rabbitmqctl list_exchanges`

#### 队列相关

- 查看队列：`rabbitmqctl list_queues`
- 删除队列：`rabbitmqctl delete_queue`
- 清空队列：`rabbitmqctl purge_queue`

#### 用户相关

- 新建用户：`rabbitmqctl add_user`
- 修改用户密码：`rabbitmqctl change_password`
- 删除用户：`rabbitmqctl delete_user`
- 查看用户：`rabbitmqctl list_users`
- 设置用户角色：`rabbitmqctl rabbitmqctl set_user_tags`

#### 应用启停

- 启动应用：`rabbitmqctl start_qpp`
- 关闭应用：`rabbitmqctl stop_app`，保留 Erlang 虚拟机（暂停）
- 关闭应用：`rabbitmqctl stop`，并关闭 Erlang 虚拟机

##  外卖订单系统

### 微服务拆分
- 订单微服务：订单获取和履行
- 商家微服务：供应商和产品管理
- 骑手微服务：送餐，骑手管理
- 结算微服务：计账与结算
- 积分微服务：积分微服务

### 业务流程图

![](https://files.mdnice.com/user/19026/65f57278-819f-4772-885f-df0687ed9073.png)

### 项目数据库设计图
![](https://files.mdnice.com/user/19026/a7a33e7e-ae65-4c29-8216-248217d26ff6.png)

### Docker 开启数据库 & FlyWay 数据表创建与数据迁移
```bash
docker run --name rabbit -e MYSQL_ROOT_PASSWORD=123 -e MYSQL_DATABASE=rabbit -e TZ=Asia/Shanghai -p 3306:3306 -d mysql
```
为了保证数据库查询能正常显示中文数据，进入到 mysql 服务器后，我们需要执行：
```sql
set names utf8mb4;
```
进入到项目的 db 目录下，执行命令：
```bash
mvn flyway:clean flyway:migrate
```

<--------------------待完成-------------------->
## 四.....
#### 如何保证消息的可靠性
- 发送方
    - 需要使用 RabbitMQ 发送端确认机制，确认消息成功发送到 RabbitMQ 并被处理
    - 需要使用 RabbitMQ 消息返回机制，若没发现目标队列，中间件会通知发送方
- 消费方
    - 需要使用 RabbitMQ 消息端确认机制，确认消息没有发生处理异常
    - 需要使用 RabbitMQ 消费端限流机制，限制消息推送速度，保障接收端服务稳定    
- RabbitMQ 自身
    - 大量堆积的消息会给 RabbitMQ 产生很大的压力，需要使用 RabbitMQ 消息过期的时间，以防止消息的大量堆积
    - 消息过期后如果直接被丢弃，无法对系统运行异常发出警报，需要使用 RabbitMQ 死信队列，收集过期消息，以提供分析
#### 发送端确认机制
发送端确认机制的目的就是为了确认消息是否有真的发送出去。
##### 什么是发送端确认机制
- 消息发送后，若中间件收到消息，会给发送端一个应答
- 生产者接收应答，用来确认这条消息是否正常发送到中间件
##### 三种确认机制
- 单条同步确认
- 多条同步确认
- 异步确认
##### 单条同步确认的实现方法
- 配置 channel，开启确认模式：`channel.confirmSelect()`
- 每发送一条消息，调用 `channel.waitForConfirms()` 方法，等待确认
##### 多条同步确认机制的实现方法
- 配置 channel，开启确认模式：`channel.confirmSelect()`
- 发送多条消息后，调用 `channel.waitForConfirms()` 方法，等待确认
##### 异步确认机制的实现方法
- 配置 channel，开启确认模式：`channel.confirmSelect()`
- 在 channel 上添加监听：addConfirmListener，发送消息后，会回调此方法，通知是否发送成功
- 异步确认有可能是单条，也有可能是多条，取决于 MQ 
## Bug report
数据库字符集不正确的解决方法：
```sql
set names utf8mb4;
```     