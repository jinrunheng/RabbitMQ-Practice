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

#### 消息返回机制
##### 消息真的被路由了么

- 消息发送后，发送端不知道消息是否被正确路由，若路由异常，消息会被丢弃
- 消息丢弃后，订单处理流程停止，业务异常
- 需要使用 RabbitMQ 消息返回机制，确认消息被正确路由

##### 消息返回机制的原理

- 消息发送后，中间件对消息进行路由
- 如果没有发现目标队列，中间件会通知发送方
- Return Listener 会被调用

##### 消息返回机制的开启方法

- 在 RabbitMQ 基础配置中有一个关键配置项：Mandatory
- Mandatory 如果为 false，RabbitMQ 将直接丢弃无法路由的消息
- Mandatory 如果为 true，RabbitMQ 才会处理无法路由的消息

#### 消费端确认机制

##### 消费端处理异常怎么办？

- 默认情况下，消费端接收消息时，消息会被自动确认（ACK）
- 消费端消息处理异常时，发送端与消息中间件无法得知消息处理情况
- 需要使用 RabbitMQ 消费端确认机制，确认消息被正确处理

##### 消费端 ACK 类型

- 自动 ACK，消费端收到消息后，会自动签收消息
- 手动 ACK，消费端收到消息后，不去自动签收，需要我们在业务代码中显示地签收消息

##### 手动 ACK 类型

- 单条手动 ACK：multiple = false
- 多条手动 ACK：multiple = true
- 推荐使用单条 ACK

##### 重回队列

- 若设置了重回队列，消息被 NACK 之后，会返回队列末尾，等待进一步被处理
- 一般不建议开启重回队列，因为第一次处理异常的消息，再次处理，基本上也是异常

#### 消费端限流机制

##### 消费端处理的过来么

- 业务高峰期，可能出现发送端与接收端性能不一致，大量消息被同时推送给接收端，造成接收端服务崩溃
- 需要使用 RabbitMQ 消费端限流机制，限制消息推送的速度，保障接收端服务稳定

##### 实际场景

- 业务高峰期，有个微服务崩溃了，崩溃期间队列挤压了大量消息，微服务上线后，收到了大量的并发消息
- 将同样多的消息推送给能力不同的副本，会导致部分副本异常

##### RabbitMQ-QoS

- 针对以上问题，RabbitMQ 开发了 QoS（服务质量保证）功能
- QoS 功能保证了在一定数目的消息未被确认前，不消费新的消息
- QoS 功能使用的前提是，不使用消费端的自动确认

##### QoS 原理

- QoS 原理是当消费端有一定数量的消息未被 ACK 确认时，RabbitMQ 不会给消费端推送新的消息
- RabbitMQ 使用 QoS 机制实现了消费端限流

##### 消费端限流机制参数设置
AMQP 协议中：

- prefetchCount：针对一个消费端最多推送多少未确认消息
- global：当为 true 时，会针对整个消费端进行限流，当为 false 时，会针对当前 channel 进行限流
- prefetchSize: 0 （单个消息大小限制，一般为 0）
- prefetchSize 与 global 两项，RabbitMQ 暂未实现

##### QoS 的关键优点

- 可以横向扩展消费者

#### 消费端限流机制

##### 队列爆满怎么办

- 默认情况下，消息进入队列后，会永远存在，直到被消费
- 大量堆积的消息会给 RabbitMQ 产生很大的压力
- 需要使用 RabbitMQ 消息过期机制，防止消息的大量堆积

##### RabbitMQ 的过期时间（TTL）

- RabbitMQ 消息的过期时间称之为 TTL（Time To Live）
- RabbitMQ 的过期时间分为消息TTL和队列 TTL
- 消息 TTL 设置了单条消息的过期时间
- 队列 TTL 设置了队列中所有消息的过期时间 

##### 如何找到适合自己的 TTL
- TTL 的设置主要考虑技术架构与业务
- TTL 应该明显长于服务的平均重启时间
- 建议 TTL 长于业务高峰期时间

##### 如何转移过期消息？

- 消息被设置了过期时间，过期后会被直接丢弃
- 直接被丢弃的消息，无法对系统运行异常发出警报
- 需要使用 RabbitMQ 死信队列，收集过期消息，以供分析

##### 什么是死信队列

- 死信队列指的是：队列被配置了 DLX 属性的队列（Dead-Letter-Exchange）
- 当一个消息变成死信（Dead Message）后，能重新被发布到另一个 Exchange，这个 Exchange 也是一个普通的交换机
- 死信被死信交换机路由后，一般进入到一个固定的队列

##### 一条消息变成死信的条件

- 消息被拒绝（reject/nack），并且 requeue = false
- 消息过期
- 队列达到了最大长度

##### 死信队列设置方法

1. 设置转发，接收死信的交换机和队列
    - Exchange:`dlx.exchange`
    - Queue:`dlx.queue`
    - RoutingKey:`#`
2. 在需要设置死信的队列中加入参数
    - `x-dead-letter-exchange = dlx.exchange`


## Bug report
数据库字符集不正确的解决方法：
```sql
set names utf8mb4;
```     