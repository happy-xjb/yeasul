package com.yealink.ui.vo;

import com.yealink.entities.Check;
import com.yealink.entities.Node;
import lombok.Data;

import java.util.List;

@Data
public class NodeAndCheckVO {
    Node node;
    List<Check> checkList;
}
