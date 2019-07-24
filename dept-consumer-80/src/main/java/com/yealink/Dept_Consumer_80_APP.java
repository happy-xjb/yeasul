package com.yealink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class Dept_Consumer_80_APP
{
    public static void main( String[] args )
    {
        SpringApplication.run(Dept_Consumer_80_APP.class,args);
    }
}
