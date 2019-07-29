package com.yealink.entities;

import lombok.Data;

@Data
public class RegisterInfo {
    /**
     * 服务实例ID
     */
    private String serviceInstanceId;

    /**
     * 节点ID
     */
    private String nodeId;

    /**
     * 数据中心名称
     */
    private String datacenter;
}