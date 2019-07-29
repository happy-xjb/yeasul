package com.yealink.init;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class NodeInit implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {

    }
}
