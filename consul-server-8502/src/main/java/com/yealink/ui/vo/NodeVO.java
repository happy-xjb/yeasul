package com.yealink.ui.vo;


import com.yealink.entities.Node;
import lombok.Data;

import java.util.List;
import java.util.Map;


/**
 * @author happy-xjb
 * @date 2019/8/1 17:26
 */

@Data
public class NodeVO {
    Node node;
    List<ServiceWithTagsVO> serviceWithTagsVOList;
    List<Map> checkList;
}
