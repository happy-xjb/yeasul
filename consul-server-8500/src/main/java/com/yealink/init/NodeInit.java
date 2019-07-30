package com.yealink.init;

import com.yealink.dao.NodeMapper;
import com.yealink.dao.RegisterInfoMapper;
import com.yealink.entities.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
    @Autowired
    RegisterInfoMapper registerInfoMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(nodeMapper.selectByAddress(address)==null){
            Node node = new Node();
            node.setNodeId(UUID.randomUUID().toString());
            node.setAddress(address);
            node.setDatacenter(datacenter);
            node.setName(nodeName);
            nodeMapper.insert(node);
        }else{
            Node node = nodeMapper.selectByAddress(address);
            node.setName(nodeName);
            node.setDatacenter(datacenter);
            nodeMapper.updateByPrimaryKeySelective(node);
            //数据库中注册信息更改，迁移到新的DC中
            registerInfoMapper.updateDatacenterByNodeId(node.getNodeId(),datacenter);
        }
    }
}
