package com.yealink.entities;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ServiceName {
    /**
    * 服务名称
    */
    private String service;
}