package com.yealink.init;

import com.yealink.dao.NodeMapper;
import com.yealink.entities.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.UUID;

@Component
@Order(1)
public class NodeInit implements ApplicationRunner {
    @Value("${consul.config.node-name}")
    private String nodeName;
    @Value("${consul.config.datacenter}")
    private String datacenter;

    @Value("${consul.debug-config.bind-address}")
    private String address;

    @Autowired
    private NodeMapper nodeMapper;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        Node node = nodeMapper.selectByAddress(address);
        if(node==null){
            Node newNode = new Node();
            //设置节点名
            if(nodeName!=null&&!nodeName.equals(""))    newNode.setName(nodeName);
            else newNode.setName(InetAddress.getLocalHost().getHostName());
            //设置数据中心
            if(datacenter!=null&&!datacenter.equals(""))    newNode.setDatacenter(datacenter);
            else newNode.setDatacenter("dc1");
            newNode.setAddress(address);
            newNode.setNodeId(UUID.randomUUID().toString());
            nodeMapper.insertSelective(newNode);
        }

    }
}
