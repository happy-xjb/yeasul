package com.yealink.ui.vo;

import com.yealink.entities.ServiceInstance;
import lombok.Data;

import java.util.List;


@Data
public class ServiceWithTagsVO {
    ServiceInstance serviceInstance;
    List<String> tags;
}
