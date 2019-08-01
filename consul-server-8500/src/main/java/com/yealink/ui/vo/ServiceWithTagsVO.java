package com.yealink.ui.vo;



import com.yealink.entities.ServiceInstance;
import lombok.Data;

import java.util.List;

/**
 * @author happy-xjb
 * @date 2019/8/1 17:28
 */

@Data
public class ServiceWithTagsVO {
    ServiceInstance serviceInstance;
    List<String> tags;
}
