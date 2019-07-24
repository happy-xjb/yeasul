package com.yealink.entities;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Node {
    /**
    * 唯一的UUID
    */
    private String nodeId;

    /**
    * 节点名称
    */
    private String name;

    /**
    * 节点IP地址
    */
    private String address;
}