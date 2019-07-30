package com.yealink.ui.service;

import com.yealink.ui.vo.NodeVO;
import com.yealink.ui.vo.ServiceVO;

import java.util.List;
import java.util.Map;

public interface UiService {
    Map<String, ServiceVO> getAllServiceDetails();
    Map<String, NodeVO> getAllNodeDetails();
}
