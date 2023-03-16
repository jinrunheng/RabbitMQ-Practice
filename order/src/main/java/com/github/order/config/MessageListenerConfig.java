package com.github.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Dooby Kim
 * @Date 2023/3/7 10:17 下午
 * @Version 1.0
 */
@Configuration
@Slf4j
public class MessageListenerConfig {

//    @Autowired
//    private OrderMessageService orderMessageService;

//    @Bean
//    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory) {
//        SimpleMessageListenerContainer messageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
//        messageListenerContainer.setQueueNames("queue.order");
//        messageListenerContainer.setConcurrentConsumers(3);
//        messageListenerContainer.setMaxConcurrentConsumers(5);
//        // 开启自动确认 ack
//        // messageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
//        // 设置回调方法，相当于 channel.basicConsume
//        // messageListenerContainer.setMessageListener(this::handle);
//
//        // 消费端开启手动确认
//        messageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//        // 消费端限流
//        messageListenerContainer.setPrefetchCount(20);
//
//        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter();
//        // 设置代理
//        messageListenerAdapter.setDelegate(orderMessageService);
//        // 设置 MessageConverter
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        jackson2JsonMessageConverter.setClassMapper(new ClassMapper() {
//            @Override
//            public void fromClass(Class<?> aClass, MessageProperties messageProperties) {
//
//            }
//
//            @Override
//            public Class<?> toClass(MessageProperties messageProperties) {
//                return OrderMessageDTO.class;
//            }
//        });
//        messageListenerAdapter.setMessageConverter(jackson2JsonMessageConverter);
//        messageListenerContainer.setMessageListener(messageListenerAdapter);
//        return messageListenerContainer;
//    }

}
