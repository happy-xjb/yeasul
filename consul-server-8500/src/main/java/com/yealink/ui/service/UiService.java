package com.yealink.ui.service;

import com.yealink.ui.vo.NodeVO;
import com.yealink.ui.vo.ServiceVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UiService {
    Map<String, ServiceVO> getAllServiceDetails();
    Map<String, NodeVO> getAllNodeDetails();
    Map<String,String>  getKVPathIndex(String currentPath);
    Set<String> getKVCatalog(String currentPath);
}
