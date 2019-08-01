package com.yealink.ui.vo;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ServiceVO {
    Set<String> tags;
    List<NodeAndCheckVO> nodeAndCheckVOList;
}
