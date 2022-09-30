package com.su.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@ComponentScan(basePackages = {"com.su"})
@MapperScan(basePackages = {"com.su.blog.dao"})
@SpringBootApplication
@CrossOrigin
public class BlogServerApplication {
    /**
     * 定义线程池
     * @return
     */
    @Bean("testThreadPool")
    public Executor asyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);//配置核心线程数
        executor.setMaxPoolSize(40);//配置最大线程数
        executor.setKeepAliveSeconds(30);
        executor.setQueueCapacity(100);//配置队列大小
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());//拒绝策略
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();//执行初始化
        return executor;
    }
    public static void main(String[] args) {
        SpringApplication.run(BlogServerApplication.class, args);
    }

}
