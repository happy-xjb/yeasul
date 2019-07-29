package com.yealink.ui.service.impl;

import com.yealink.ui.service.UiService;
import com.yealink.ui.vo.ServiceVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UiServiceImplTest {

    @Autowired
    UiService uiService;

    @Test
    public void getAllServiceDetails() {
        Map<String, ServiceVO> allServiceDetails = uiService.getAllServiceDetails();
        System.out.println(allServiceDetails);
    }
}