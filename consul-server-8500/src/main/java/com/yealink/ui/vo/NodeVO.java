package com.yealink.ui.vo;

import com.yealink.entities.Check;
import com.yealink.entities.Node;
import lombok.Data;

import java.util.List;

@Data
public class NodeVO {
    Node node;
    List<ServiceWithTagsVO> serviceWithTagsVOList;
    List<Check> checkList;
}
