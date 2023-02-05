package com.github.order.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 异步线程池相关配置
 *
 * @Author Dooby Kim
 * @Date 2022/10/31 8:52 下午
 * @Version 1.0
 */
@Configuration
@EnableAsync
public class AsyncTaskConfig implements AsyncConfigurer {

    @Bean
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // 设置线程池的核心线程数
        threadPoolTaskExecutor.setCorePoolSize(10);
        // 设置线程池最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(100);
        // 设置缓冲队列的长度
        threadPoolTaskExecutor.setQueueCapacity(10);
        // 设置线程池关闭时，是否要等待所有线程结束后再关闭
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        // 设置线程池等待所有线程结束的时间
        threadPoolTaskExecutor.setAwaitTerminationSeconds(60);
        // 设置所有线程的前缀名称
        threadPoolTaskExecutor.setThreadNamePrefix("Rabbit-Async-");
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }

}
