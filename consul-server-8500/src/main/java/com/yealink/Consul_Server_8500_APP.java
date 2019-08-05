package com.yealink;

import com.yealink.config.Self;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@MapperScan("com.yealink.dao")
@EnableConfigurationProperties(Self.class)
@PropertySource("classpath:application.yml")
public class Consul_Server_8500_APP extends SpringBootServletInitializer
{
    public static void main( String[] args )
    {
        SpringApplication.run(Consul_Server_8500_APP.class,args);
    }

    @Override//为了打包springboot项目
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }
}
