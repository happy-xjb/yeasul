package com.yealink.service;


import com.ecwid.consul.v1.kv.model.GetBinaryValue;

import java.util.List;

/**
 * @author happy-xjb
 * @date 2019/8/7 10:52
 */
public interface KeyValueService {
    List<GetBinaryValue> getBinaryKeyValue(String key);

    void setKeyValue(String key, String value);

    void deleteKeyValue(String key);
}
