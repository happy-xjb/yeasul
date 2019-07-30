package com.yealink.ui.controller;

import com.yealink.dao.RegisterInfoMapper;
import com.yealink.dao.ServiceNameMapper;
import com.yealink.dao.ServiceTagMapper;
import com.yealink.ui.service.UiService;
import com.yealink.ui.vo.ServiceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
public class ServiceController{
    @Autowired
    ServiceNameMapper serviceNameMapper;
    @Autowired
    ServiceTagMapper serviceTagMapper;
    @Autowired
    UiService uiService;
    @Autowired
    RegisterInfoMapper registerInfoMapper;
    @Value("${consul.config.datacenter}")
    String datacenter;

    @RequestMapping("/ui/services")
    public String services(Model model){
        List<String> services = registerInfoMapper.selectAllServiceInDatacenter(datacenter);
        Map<String, ServiceVO> serviceMap = uiService.getAllServiceDetails();
        model.addAttribute("services",services);
        model.addAttribute("serviceMap",serviceMap);
        return "index";
    }
}
