package com.yealink.entities;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ServiceInstance {
    /**
    * 服务实例ID
    */
    private String serviceInstanceId;

    /**
    * 服务名称
    */
    private String service;

    /**
    * 服务实例IP地址
    */
    private String address;

    private Integer port;
}