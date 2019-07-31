package com.yealink.controller.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.yealink.config.Self;
import com.yealink.controller.AgentController;
import com.yealink.entities.Node;
import com.yealink.service.AgentService;
import com.yealink.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;


@RestController
@Slf4j
public class AgentHttpController extends AgentController {
    @Autowired
    Gson gson;

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

    /**
     * 接收想要加入的节点信息
     * @param request
     */
    @PutMapping("/acceptJoin")
    public void acceptJoin(HttpServletRequest request){
        //接收到想要加入集群的node信息
        Node node = JsonUtil.getObjectFromJson(request, Node.class);
        //将当前集群的所有节点信息发送给想加入集群的节点
        agentService.sendAllNodesInClusterTo(node);
        //当前集群的所有节点的Node表均添加新加入的Node
        agentService.acceptNewNode(node);
    }

    /**
     * 加入集群时，接收集群中的节点列表，然后将列表添加到自己的表中
     * @param request
     */
    @PutMapping("/clusterNodes")
    public void getClusterNodes(HttpServletRequest request){
        try {
            String json = request.getReader().readLine();
            List<Node> nodeList = JsonUtil.getNodeListFromJson(json);
            System.out.println("A端接收到的list为"+nodeList);
            //把集群的node列表复制给自己
            agentService.copyNodeListToMyself(nodeList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用于向集群里插入一个新的节点
     */
    @PutMapping("/insertNode")
    public void insertNode(HttpServletRequest request){
        Node newNode = JsonUtil.getObjectFromJson(request, Node.class);
        System.out.println("准备插入表的node为："+newNode);
        agentService.insertNewNode(newNode);
        log.info("[Cluster] "+newNode+" join the cluster successfully.");
    }
}
