package com.yealink.ui.controller;

import com.yealink.dao.NodeMapper;
import com.yealink.dao.RegisterInfoMapper;
import com.yealink.ui.service.UiService;
import com.yealink.ui.vo.NodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
public class NodeController {
    @Autowired
    NodeMapper nodeMapper;
    @Autowired
    UiService uiService;

    @Value("${consul.config.datacenter}")
    String datacenter;

    @RequestMapping("/ui/nodes")
    public String nodes(Model model){
        List<String> nodes = nodeMapper.selectAllNodeNameInDatacenter(datacenter);
        model.addAttribute("nodes",nodes);
        Map<String, NodeVO> nodeMap = uiService.getAllNodeDetails();
        model.addAttribute("nodeMap",nodeMap);
        return "nodes";
    }
}
