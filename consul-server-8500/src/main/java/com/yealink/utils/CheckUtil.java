package com.yealink.utils;

import com.ecwid.consul.v1.agent.model.NewService;
import com.google.gson.Gson;
import com.yealink.dao.CheckMapper;
import com.yealink.entities.Check;
import com.yealink.vo.ActuatorHealthVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CheckUtil {
    @Autowired
    private Gson gson;

    @Autowired
    private CheckMapper checkMapper;

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    public void startHttpCheck(NewService newService){
        NewService.Check newServiceCheck = newService.getCheck();
        String url = newServiceCheck.getHttp();
        String interval = newServiceCheck.getInterval();    //循环时间
        String timeout = newServiceCheck.getTimeout();  //超时时间
        long interval_timeNum = TimeUtil.getTimeNum(interval);   //循环时间数字部分
        TimeUnit interval_timeUnit = TimeUtil.getTimeUnit(interval);    //循环时间单位
        long timeout_timeNum = TimeUtil.getTimeNum(timeout); //超时时间数字部分
        TimeUnit timeout_timeUnit = TimeUtil.getTimeUnit(timeout);  //超时时间单位
        Check check = new Check().setCheckId("service:"+newService.getId())
                .setName("Service '"+newService.getName()+"' check")
                .setServiceId(newService.getId())
                .setServiceName(newService.getName());

        //检查check是否已经存在
        if(checkMapper.selectByPrimaryKey(check.getCheckId())==null)    checkMapper.insertSelective(check);
        int oldValue = 1;    //设定check初始状态为1，代表passing
        int newValue = 1;
        //开启定时任务
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout((int) TimeUnit.MILLISECONDS.convert(timeout_timeNum, timeout_timeUnit))
                    .setConnectionRequestTimeout((int) TimeUnit.MILLISECONDS.convert(timeout_timeNum, timeout_timeUnit)).build();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig);
            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                check.setOutput("GET " + url +" " + response.getStatusLine());
                if (entity != null) {
                    String json = EntityUtils.toString(entity);
                    ActuatorHealthVO actuatorHealthVO = gson.fromJson(json, ActuatorHealthVO.class);
                    if (actuatorHealthVO.getStatus().equals("UP")) {
                        check.setOutput("HTTP GET " + url + response.getStatusLine()).setStatus("passing");
                        checkMapper.updateByPrimaryKey(check);
                    }
                }
                log.info("[Service] check pass"+check);
            } catch (IOException e) {
                e.printStackTrace();
                log.warn("【服务异常】" + check);
                check.setStatus("failing").setOutput("服务异常");
                checkMapper.updateByPrimaryKey(check);
            }
        }, 0, interval_timeNum, interval_timeUnit);

        System.out.println("执行至此");
    }

    //启动TCP检查

    /**
     *
     * @param checkId   检查id
     * @param host  ip地址
     * @param port  端口号
     * @param interval  循环检查时间
     * @param timeout   超时时间，毫秒
     * @param timeUnit  循环检查时间的单位
     */
    public void startTcpCheck(String checkId,String host,int port,long interval,long timeout,TimeUnit timeUnit){
        scheduledExecutorService.scheduleAtFixedRate(()->{
            Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(host,port), (int) timeout);
                log.info("agent: socket connection success '"+host+":"+port);
                //TODO  将check状态变成passing,更新output
                checkMapper.updateStatusToPassingByPrimaryKey(checkId);
                checkMapper.updateOutputByPrimaryKey(checkId,"socket connection success '"+host+":"+port);
            } catch (IOException e) {
                log.warn("agent: socket connection failed '"+host+":"+port);
                //TODO 将check状态变成failing，更新output
                checkMapper.updateStatusToFailingByPrimaryKey(checkId);
                checkMapper.updateOutputByPrimaryKey(checkId,"socket connection failed '"+host+":"+port);
            }
            //关闭socket
            if(!socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },0,interval,timeUnit);
    }
}
