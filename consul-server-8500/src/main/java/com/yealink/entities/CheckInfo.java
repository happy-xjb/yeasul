package com.yealink.entities;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CheckInfo {
    /**
     * 检查ID，格式为service:服务实例ID
     */
    private String checkId;

    /**
     * 检查方式，目前支持TCP和HTTP，有两种值http，tcp
     */
    private String kind;

    /**
     * 检查的url
     */
    private String url;

    /**
     * 循环检查时间
     */
    private String interval;

    /**
     * 超时时间
     */
    private String timeout;

    /**
     * 检查的节点名称
     */
    private String node;
}