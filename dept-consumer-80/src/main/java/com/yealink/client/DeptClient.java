package com.yealink.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("pay-provider")
public interface DeptClient {
    @RequestMapping("/hello")
    String  callHello();
}
