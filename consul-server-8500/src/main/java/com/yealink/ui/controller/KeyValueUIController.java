package com.yealink.ui.controller;

import com.yealink.ui.service.UiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author happy-xjb
 * @date 2019/8/8 15:02
 */

@Controller
public class KeyValueUIController {
    @Autowired
    UiService uiService;

    @GetMapping("/ui/kv/**")
    public String getKV(Model model, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        String substring = requestURI.substring(7);
        model.addAttribute("pathMap",uiService.getKVPathIndex(substring));
        model.addAttribute("catalog",uiService.getKVCatalog(substring));
        model.addAttribute("current",substring);
        return "keyValue";
    }



}
