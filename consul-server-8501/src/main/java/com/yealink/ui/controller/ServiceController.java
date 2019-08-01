package com.yealink.ui.controller;

import com.yealink.dao.ServiceNameMapper;
import com.yealink.dao.ServiceTagMapper;
import com.yealink.ui.service.UiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Set;

@Controller
public class ServiceController{
    @Autowired
    ServiceNameMapper serviceNameMapper;
    @Autowired
    ServiceTagMapper serviceTagMapper;
    @Autowired
    UiService uiService;

    //返回到services页面
    @RequestMapping("/ui/services")
    public String services(Model model){
        Set<String> services = uiService.getAllServices();
//        Map<String, ServiceVO> serviceMap = uiService.getAllServiceDetails();
        model.addAttribute("services",services);
//        model.addAttribute("serviceMap",serviceMap);
        return "index";
    }

    @RequestMapping("/ui/services/{service}")
    public String getService(@PathVariable String service){
        return "forward:/v1/health/service/"+service;
    }

    //返回本节点的所有服务名称
    @RequestMapping("/ui/myServices")
    @ResponseBody
    public List<String> myServices(){
        return uiService.getMyServices();
    }




}
