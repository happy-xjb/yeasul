package com.yealink.service.impl;

import com.ecwid.consul.v1.kv.model.GetBinaryValue;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.yealink.dao.KeyValueMapper;
import com.yealink.entities.KeyValue;
import com.yealink.service.KeyValueService;
import com.yealink.utils.Base64Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author happy-xjb
 * @date 2019/8/7 10:54
 */
@Service
public class KeyValueServiceImpl implements KeyValueService {
    @Autowired
    KeyValueMapper keyValueMapper;
    @Value("${consul.config.datacenter}")
    String datacenter;


    @Override
    public List<GetBinaryValue> getBinaryKeyValue(String key) {
        List<GetBinaryValue> list = new ArrayList<>();
        KeyValue keyValue = keyValueMapper.selectByKeyAndDatacenter(key,datacenter);
        if(keyValue!=null){
            GetBinaryValue binaryValue = new GetBinaryValue();
            binaryValue.setKey(keyValue.getKey());
            binaryValue.setValue(keyValue.getValue().getBytes());
            list.add(binaryValue);
        }
        return list;
    }

    @Override
    public void setKeyValue(String key, String value) {
        KeyValue keyValue = new KeyValue();
        keyValue.setKey(key);
        keyValue.setDatacenter(datacenter);
        String base64Value = Base64Utils.encode(value);
        keyValue.setValue(base64Value);
        KeyValue byKeyAndDatacenter = keyValueMapper.selectByKeyAndDatacenter(key, datacenter);

        if(byKeyAndDatacenter==null)    keyValueMapper.insertSelective(keyValue);
        else keyValueMapper.update(keyValue);
    }

    @Override
    public void deleteKeyValue(String key) {
        keyValueMapper.delectByKeyAndDataceneter(key,datacenter);
    }


}
