package com.yealink.controller;

import com.yealink.config.Self;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/status")
public class StatusController {

    @Value("${server.port}")
    private int port;

    @Autowired
    Self self;
    @GetMapping("/leader")
    public String getLeaderStatus(){

        String leader = "\"127.0.0.1:"+port+"\"";
        return leader;
    }

    @GetMapping("node")
    public void getNode(){
        System.out.println(self);
    }

}
