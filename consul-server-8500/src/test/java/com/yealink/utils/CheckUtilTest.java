package com.yealink.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
@SpringBootTest
@RunWith(SpringRunner.class)
public class CheckUtilTest {
    @Autowired
    CheckUtil checkUtil;

    @Test
    public void startHttpCheck() {
    }

    @Test
    public void startTcpCheck() {
    }

    @Test
    public void startHttpCheck1() {
        checkUtil.startHttpCheck("mem2","https://www.baidu.com",5,5, TimeUnit.SECONDS);
    }
}