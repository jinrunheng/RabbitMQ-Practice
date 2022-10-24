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
2. `brew install rabbitmq`
3. 启动：`brew services start rabbitmq`
### Docker
```bash
docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.10-management
```

