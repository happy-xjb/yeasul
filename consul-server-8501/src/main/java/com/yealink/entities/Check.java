package com.yealink.entities;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Check {
    /**
    * 检查ID，格式为service:服务实例ID
    */
    private String checkId;

    /**
    * 检查的节点名称
    */
    private String node;

    /**
    * 检查名称 格式为Service '服务名称' check
    */
    private String name;

    /**
    * 检查状态，passing,warning,failing
    */
    private String status;

    /**
    * 笔记，一般为空
    */
    private String notes;

    /**
    * 检查输出为可理解的文字，如HTTP GET http://10.83.0.125:9532: 200 OK Output: Apollo
    */
    private String output;

    /**
    * 检查的服务ID
    */
    private String serviceId;

    /**
    * 检查的服务名称
    */
    private String serviceName;
}