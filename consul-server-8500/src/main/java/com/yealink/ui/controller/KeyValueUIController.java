package com.yealink.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    @GetMapping("/ui/kv/**")
    public String getKV(Model model, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        String substring = requestURI.substring(7);
        List<String> pathList = Arrays.asList(substring.split("/"));
        model.addAttribute("pathList",pathList);

        return "keyValue";
    }
}
