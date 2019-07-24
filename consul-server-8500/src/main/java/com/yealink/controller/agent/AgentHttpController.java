package com.yealink.controller.agent;

import com.yealink.config.Self;
import com.yealink.controller.AgentController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgentHttpController extends AgentController {
    @Autowired
    Self self;

    @GetMapping("/self")
    public Self getAgentSelf(){
        return self;
    }

}
