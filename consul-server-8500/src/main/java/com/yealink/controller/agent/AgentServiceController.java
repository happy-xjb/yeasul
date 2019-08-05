package com.yealink.controller.agent;

import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.agent.model.Service;
import com.yealink.controller.AgentController;
import com.yealink.service.AgentService;
import com.yealink.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class AgentServiceController extends AgentController {

    @Autowired
    private AgentService agentService;

    @PutMapping("/service/register")
    public void registerService(HttpServletRequest request){
        NewService newService = JsonUtil.getObjectFromJson(request, NewService.class);
        agentService.agentServiceRegister(newService);
    }

    @GetMapping("/services")
    public Map<String, Service> getServices(){
        return agentService.getAgentServices();
    }

    @PutMapping("/service/deregister/{serviceId}")
    public void agentServiceDeregister(@PathVariable String serviceId){
//        System.out.println("进入了方法");
        agentService.agentServiceDeregister(serviceId);
    }
}
