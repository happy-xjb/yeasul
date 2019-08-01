package com.yealink.ui.vo;


import com.yealink.entities.Check;
import com.yealink.entities.Node;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author happy-xjb
 * @date 2019/8/1 16:02
 */
@Data
public class NodeAndCheckVO {
    Node node;

    //check列表，每个map存储着一条check记录的信息
    List<Map> checkList;
}