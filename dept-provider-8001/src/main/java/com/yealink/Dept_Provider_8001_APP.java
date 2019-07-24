package com.yealink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
public class Dept_Provider_8001_APP
{
    public static void main( String[] args )
    {
        SpringApplication.run(Dept_Provider_8001_APP.class,args);
    }
}
