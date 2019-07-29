package com.yealink.entities;

import lombok.Data;

@Data
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

    /**
     * 数据中心名称
     */
    private String datacenter;
}