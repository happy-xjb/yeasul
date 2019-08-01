package com.yealink.ui.service;

import com.yealink.ui.vo.ServiceVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UiService {

    Map<String, ServiceVO> getAllServiceDetails();

    //返回的是集群中所有的名称集合
    Set<String> getAllServices();

    //返回本节点所有的服务名称
    List<String> getMyServices();
}
