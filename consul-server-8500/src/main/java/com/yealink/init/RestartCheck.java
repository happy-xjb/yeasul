package com.yealink.init;

import com.yealink.dao.CheckInfoMapper;
import com.yealink.entities.CheckInfo;
import com.yealink.utils.CheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用于重启应用时，重新启动健康检查
 * @author happy-xjb
 * @date 2019/7/30 20:17
 */

@Component
@Order(2)
public class RestartCheck implements ApplicationRunner {
    @Autowired
    CheckInfoMapper checkInfoMapper;

    @Value("${consul.config.node-name}")
    String nodeName;

    @Autowired
    CheckUtil checkUtil;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<CheckInfo> checkInfoList = checkInfoMapper.selectAll();
        for(CheckInfo checkInfo : checkInfoList){
            if(checkInfo.getKind().equals("http")){
                checkUtil.startHttpCheck(checkInfo.getCheckId(),checkInfo.getUrl(),checkInfo.getInterval(),checkInfo.getTimeout());
            }else if(checkInfo.getKind().equals("tcp")){
                checkUtil.startTcpCheck(checkInfo.getCheckId(),checkInfo.getUrl(),checkInfo.getInterval(),checkInfo.getTimeout());
            }
        }
    }
}

