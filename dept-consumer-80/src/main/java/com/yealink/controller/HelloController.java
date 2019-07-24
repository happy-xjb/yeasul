package com.yealink.controller;

import com.yealink.client.DeptClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @Autowired
    DeptClient client;

    @RequestMapping("/hello")
    public String callHello(){
        return client.callHello();
    }
}
