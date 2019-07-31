package com.yealink.ui.vo;

import com.yealink.entities.Check;
import com.yealink.entities.ServiceInstance;
import lombok.Data;

import java.util.List;

@Data
public class ServiceAndCheckVO {
    ServiceInstance serviceInstance;
    List<Check> checks;
    List<String> tags;
}
