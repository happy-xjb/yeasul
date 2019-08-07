package com.yealink.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    @RequestMapping({"/","/index","/ui"})
    public String index(Model model){
        return "redirect:/ui/services";
    }
}
