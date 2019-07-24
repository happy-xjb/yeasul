package com.yealink;

import com.yealink.config.Self;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@MapperScan("com.yealink.dao")
@EnableConfigurationProperties(Self.class)
@PropertySource("classpath:application.yml")
public class Consul_Server_8500_APP
{
    public static void main( String[] args )
    {
        SpringApplication.run(Consul_Server_8500_APP.class,args);
    }
}
