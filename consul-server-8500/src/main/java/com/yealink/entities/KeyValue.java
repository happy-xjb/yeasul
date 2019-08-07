package com.yealink.entities;

import lombok.Data;

@Data
public class KeyValue {
    /**
    * 数据中心名称
    */
    private String datacenter;

    /**
    * key
    */
    private String key;

    /**
    * value
    */
    private String value;
}