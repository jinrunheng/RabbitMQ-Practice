# RabbitMQ-Practice
RabbitMQ 实战
## 理解消息中间件，为什么要使用消息中间件？
- 异步处理
- 系统解耦
- 流量削峰和控流
## 主流消息中间件怎么选？
- ActiveMQ
- RabbitMQ
- RocketMQ
- Kafka

### ActiveMQ
- 优点
    - 基于 JAVA，跨平台运行
    - 可以用 JDBC 连接多种数据库
    - 有完善的界面，监控，安全机制
    - 自动重连和错误重试
- 缺点
    - 社区活跃度低
    - Apache 目前重心放在了 Apollo，对 ActiveMQ 维护少
    - 不适合上千个队列的应用场景
### RabbitMQ
- 优点
    - 当前最主流的消息中间件，基于 Erlang，支持高并发
    - 高可靠性，支持发送确认，投递确认等特性
    - 高可用性，支持镜像队列
    - 社区活跃度高
- 缺点
    - Erlang 语言较为小众，不利于二次开发
    - 代理架构下，中央节点增加了延迟，影响性能
    - 使用 AMQP 协议，使用起来有一定学习成本
### RocketMQ        
- 优点
    - 基于 Java，方便二次开发
    - 单机支持一万以上的持久化队列
    - 内存与磁盘都有一份数据，保持性能 + 高可用
    - 开发度活跃，版本更新快
- 缺点
    - 客户端种类不多，较成熟的是 Java，C++ 以及 Go
    - 没有 Web 管理界面，提供了一个 CLI
### Kafka
- 优点
    - 原生的分布式系统
    - 零拷贝技术，减少 IO 操作步骤，提高系统吞吐量
    - 快速持久化；可以在 O(1) 的系统开销下进行消息持久化
    - 支持数据批量发送和拉取                
- 缺点
    - 单机超过 64 个队列/分区时，性能明显劣化
    - 使用短轮询方式，实时性取决于轮询间隔时间
    - 消费失败不支持重试
    - 可靠性比较差，Kafka 更适合大数据系统，譬如推荐功能；订单系统则不太合适
## RabbitMQ 为什么性能高？    
主要原因是，RabbitMQ 底层使用 Erlang
- Erlang 是一门通用的面向并发的编程语言，适用于分布式系统
- Erlang 基于虚拟机解释运行，跨平台部署
- Erlang 进程间上下文切换效率远高于 C 语言
- Erlang 有着和原生 Socket 一样的延迟
## 什么是 AMQP 协议？            
- AMQP 协议作为 RabbitMQ 的规范，规定了 RabbitMQ 对外接口
- 学会了 AMQP 协议的使用，就基本掌握了 RabbitMQ 的使用
- 学会了 AMQP 协议的概念，就基本掌握了 RabbitMQ 的核心概念

![](https://files.mdnice.com/user/19026/41852710-0f28-4a91-9596-4744f1fa58fa.png)

名词解释：
- Broker：接收和分发消息的应用，RabbitMQ 就是 Message Broker
- Virtual Host：虚拟 Broker，将多个单元隔离开
- Connection：publisher/consumer 和 Broker 之间的 TCP 连接
- Channel：Connection 内部建立的逻辑连接，通常每个线程创建单独的 Channel
- Routing Key：路由键，用来指示消息的路由转发，相当于快递的地址
- Exchange：交换机，相当于快递的分拨中心
- Queue：消息队列，消息最终被送到这里等待 Consumer 取走消费

在 AMQP 协议或是 RabbitMQ 的实现中，最核心的组件是 Exchange


#### Exchange 的作用
- Exchange 承担着 RabbitMQ 的核心功能——路由转发
- Exchange 的功能是根据绑定关系和路由键为消息提供路由，将消息转发至相应的队列
- Exchange 有四种类型
    - Direct
    - Topic
    - Fanout
    - Headers
    
    其中 Headers 使用的很少，主要以其他三种为主。    
#### Direct Exchange
Message 中的 Routing Key 如果和 Binding Key 一致，Direct Exchange 则将消息发到对应的 Queue 中
#### Fanout Exchange
每个发到 Fanout Exchange 的消息都会分发（广播）到所有绑定的 Queue 上去
#### Topic Exchange
根据 Routing Key 及通配规则，Topic Exchange 将消息分发到目标 Queue 中
通配规则分为：
1. 全匹配：与 Direct 类似
2. Binding Key 中的 `#` 表示可以匹配任意个数的 word
3. Binding Key 中的 `*` 表示可以匹配任意一个 word
   
#### 总结：
Exchange 主要的，最常用的只有三种类型：Direct/Topic/Fanout
- Direct(直接路由)：Routing Key = Binding Key，容易配置和使用
- Fanout(广播路由)：群发绑定的所有队列，适用于消息广播
- Topic(话题路由)：功能较为复杂，但能降级为 Direct，所以建议优先使用，为以后拓展留余地

模拟 RabbitMQ 发消息：[RabbitMQ Simulator](http://tryrabbitmq.com/)    

## RabbitMQ 快速安装
            
### MacOS 安装
1. `brew update`
2. `brew install rabbitmq`;mac 默认的下载路径为：`/usr/local/Cellar/rabbitmq/`
3. 启动：`brew services start rabbitmq`
4. 后台启动：`rabbitmq-server -detached `
5. 直接启动：`rabbitmq-server`
6. 关闭服务：`rabbitmqctl stop`
### Docker
```bash
docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.10-management
```

## RabbitMQ 网页端管理控制台

- 启动应用：`rabbitmq-plugins enable rabbitmq_management`
- 浏览器打开：`127.0.0.1:15672`
- 默认用户名/密码：`guest/guest`

建议初始化用户名和密码,进入到 sbin 目录下，执行命令：
```bash
rabbitmqctl add_user admin 123456 #用户名admin，密码123456
rabbitmqctl set_user_tags admin administrator #设置用户最高操作权限
```
这样用户名和登陆密码为：`admin/123456`

## RabbitMQ 常用命令行

### 状态查看

- 查看状态:`rabbitmqctl status`
- 查看绑定：`rabbitmqctl list_bindings`
- 查看 channel：`rabbitmqctl list_channels`
- 查看 connection：`rabbitmqctl list_connections`
- 查看消费者：`rabbitmqctl list_consumers`
- 查看交换机：`rabbitmqctl list_exchanges`

### 队列相关

- 查看队列：`rabbitmqctl list_queues`
- 删除队列：`rabbitmqctl delete_queue`
- 清空队列：`rabbitmqctl purge_queue`

### 用户相关

- 新建用户：`rabbitmqctl add_user`
- 修改用户密码：`rabbitmqctl change_password`
- 删除用户：`rabbitmqctl delete_user`
- 查看用户：`rabbitmqctl list_users`
- 设置用户角色：`rabbitmqctl rabbitmqctl set_user_tags`

### 应用启停

- 启动应用：`rabbitmqctl start_qpp`
- 关闭应用：`rabbitmqctl stop_app`，保留 Erlang 虚拟机（暂停）
- 关闭应用：`rabbitmqctl stop`，并关闭 Erlang 虚拟机

##  外卖订单系统

### 需求分析与架构设计
1. 一个外卖后端系统，用户可以在线下单外卖
2. 用户下单后，可以实时查询订单的进度
3. 系统可以承受短时间内大量的并发请求
### 架构设计
1. 使用微服务系统，组件之间充分解耦
2. 使用消息中间件，解耦业务逻辑
3. 使用数据库，持久化业务数据
### 什么是微服务架构
将应用程序构建为松耦合，可独立部署的一组服务
- 服务：一个单一的，可独立部署的软件组件，实现了一些有用的功能
- 松耦合：封装服务的实现细节，通过 API 调用
### 如何拆分微服务
- 根据系统操作进行拆分
- 根据业务能力进行微服务拆分
- 根据子域进行微服务拆分
### 微服务拆分
- 订单微服务：订单获取和履行
- 商家微服务：供应商和产品管理
- 骑手微服务：送餐，骑手管理
- 结算微服务：计账与结算
- 积分微服务：积分微服务
### 业务流程

![](https://files.mdnice.com/user/19026/65f57278-819f-4772-885f-df0687ed9073.png)
### 接口需求

- 新建订单接口
- 查询订单接口
- 接口采用 REST 风格

### 数据库设计与项目搭建

#### 微服务数据库的设计原则
- 每个微服务使用自己的数据库
- 不要使用共享数据库的方式进行通信
- 不要使用外键，对于数据量非常少的表谨慎使用索引
#### 建表语句
DELIVERYMAN.sql:
```sql
CREATE TABLE `deliveryman` (
    `id` int not null auto_increment comment `骑手 ID`
)
```