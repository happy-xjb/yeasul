package com.yealink.entities;

import lombok.Data;

@Data
public class ServiceTag {
    /**
     * 服务名称
     */
    private String service;

    /**
     * 标签值
     */
    private String value;
    /**
     * 服务实例ID
     */
    private String serviceId;
}