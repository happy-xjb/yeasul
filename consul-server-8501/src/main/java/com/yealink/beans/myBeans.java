package com.yealink.beans;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledThreadPoolExecutor;

@Configuration
public class myBeans {

    @Bean
    public ScheduledThreadPoolExecutor getCheckSchedulePool() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
        return executor;
    }

    @Bean
    public CloseableHttpClient getHttpClient(){
        return HttpClientBuilder.create().build();
    }


}
