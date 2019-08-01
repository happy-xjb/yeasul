package com.yealink.ui.controller;


import com.yealink.dao.NodeMapper;
import com.yealink.entities.Check;
import com.yealink.ui.service.UiService;
import com.yealink.ui.vo.NodeVO;
import com.yealink.ui.vo.ServiceAndCheckVO;
import com.yealink.ui.vo.ServiceWithTagsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
/**
 * @author happy-xjb
 * @date 2019/8/1 17:34
 */

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
        List<String> nodes = nodeMapper.selectAllNodeName();
        model.addAttribute("nodes",nodes);
        Map<String, NodeVO> nodeMap = uiService.getAllNodeDetails();
        model.addAttribute("nodeMap",nodeMap);
        return "nodes";
    }

    /**
     * 返回本节点的所有check
     */
    @RequestMapping("/ui/nodes/myChecks")
    @ResponseBody
    public List<Check> myChecks(){
        return uiService.getMyChecks();
    }

    /**
     * 返回本节点注册的所有服务实例，并带上各自的tags
     */
    @RequestMapping("/ui/nodes/myServiceInstances")
    @ResponseBody
    public List<ServiceWithTagsVO> myServiceInstances(){
        return uiService.getMyServiceInstances();
    }
}