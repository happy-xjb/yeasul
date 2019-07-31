package com.yealink.controller.agent;

import com.yealink.config.Self;
import com.yealink.controller.AgentController;
import com.yealink.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgentHttpController extends AgentController {
    @Autowired
    Self self;
    @Autowired
    AgentService agentService;

    @GetMapping("/self")
    public Self getAgentSelf(){
        return self;
    }

    /**
     *尝试加入指定IP地址所在的集群，端口号默认为8500
     * @param address   IP地址
     */
    @PutMapping("/join/{address}")
    public void agentJoin(@PathVariable("address") String address){
        System.out.println("传入的address为："+address);
        agentService.agentJoin(address);
    }

    @PutMapping("/join/{address}/{port}")
    public void agentJoin(@PathVariable("address") String address,@PathVariable("port") int port){
        System.out.println("传入的address为："+address);
        System.out.println("传入的port为："+port);
        agentService.agentJoin(address,port);
    }
}
