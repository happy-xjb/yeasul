package com.yealink.ui.vo;

import lombok.Data;

import java.util.List;

@Data
public class ServiceVO {
    List<String> tags;
    List<NodeAndCheckVO> nodeAndCheckVOList;

}
