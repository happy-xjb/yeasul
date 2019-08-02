package com.yealink.utils;

import com.ecwid.consul.v1.agent.model.NewService;
import com.google.gson.Gson;
import com.yealink.dao.CheckInfoMapper;
import com.yealink.dao.CheckMapper;
import com.yealink.entities.Check;
import com.yealink.entities.CheckInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
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
    @Value("${consul.config.node-name}")
    String nodeName;

    @Autowired
    private Gson gson;

    @Autowired
    private CheckMapper checkMapper;

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private CheckInfoMapper checkInfoMapper;

    /**
     * 为新注册的服务开启检查
     * @param newService    新注册的服务
     */
    public void startHttpCheck(NewService newService){
        NewService.Check newServiceCheck = newService.getCheck();
        String url = newServiceCheck.getHttp();
        log.info("[Check] Start HTTP Check : "+url);
        String interval = newServiceCheck.getInterval();    //循环时间
        String timeout = newServiceCheck.getTimeout();  //超时时间
        long interval_timeNum = TimeUtil.getTimeNum(interval);   //循环时间数字部分
        TimeUnit interval_timeUnit = TimeUtil.getTimeUnit(interval);    //循环时间单位
        long timeout_timeNum = TimeUtil.getTimeNum(timeout); //超时时间数字部分
        TimeUnit timeout_timeUnit = TimeUtil.getTimeUnit(timeout);  //超时时间单位
        Check check = new Check().setCheckId("service:"+newService.getId())
                .setName("Service '"+newService.getName()+"' check")
                .setServiceId(newService.getId())
                .setServiceName(newService.getName())
                .setNode(nodeName);

        //Check信息持久化，持久化url，interval,timeout等等到数据库
        CheckInfo checkInfo = new CheckInfo();
        checkInfo.setCheckId(check.getCheckId()).setInterval(newServiceCheck.getInterval()).setKind("http").setNode(nodeName).setTimeout(newServiceCheck.getTimeout()).setUrl(url);
        //检查checkInfo是否已经存在
        if(checkInfoMapper.selectByPrimaryKey(checkInfo.getCheckId())==null)    checkInfoMapper.insertSelective(checkInfo);
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
                    Health health = gson.fromJson(json, Health.class);
                    if (health.getStatus().getCode().equals(Status.UP.toString())) {
                        check.setOutput("HTTP GET " + url + " "+ response.getStatusLine()).setStatus("passing");
                        checkMapper.updateByPrimaryKey(check);
                    }
                }
                log.info("[Service] check pass "+check);
            } catch (Exception e) {
                check.setStatus("failing").setOutput("HTTP GET " + url + " "+ "refused");
                checkMapper.updateByPrimaryKey(check);
                log.warn("【服务异常】" + check);
            }
        }, 0, interval_timeNum, interval_timeUnit);

    }

    /**
     * 启动Tcp健康检查
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

    /**
     * 启动Tcp健康检查
     * @param checkId   检查id
     * @param url   检查url
     * @param interval  循环检查时间，字符串，带单位
     * @param timeout   超时时间，字符串，带单位
     */
    public void startTcpCheck(String checkId,String url,String interval,String timeout){
        String[] s = url.split(":");
        String host = s[0];
        int port = Integer.parseInt(s[1]);
        long timeout_ms = TimeUnit.MILLISECONDS.convert(TimeUtil.getTimeNum(timeout), TimeUtil.getTimeUnit(timeout));
        startTcpCheck(checkId,host,port,TimeUtil.getTimeNum(interval),timeout_ms,TimeUtil.getTimeUnit(interval));
    }

    /**
     * 启动HTTP检查，使用GET请求
     * @param checkId   checkId
     * @param checkUrl  检查的URL路径
     * @param interval  循环时间
     * @param timeout   超时时间，单位毫秒
     * @param timeUnit  循环时间的单位
     */
    public void startHttpCheck(String checkId,String checkUrl,long interval,long timeout,TimeUnit timeUnit){
        Check check = checkMapper.selectByPrimaryKey(checkId);
        //开启定时任务
        scheduledExecutorService.scheduleAtFixedRate(()->{
            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout((int) timeout).setConnectTimeout((int) timeout).build();
            HttpGet httpGet = new HttpGet(checkUrl);
            httpGet.setConfig(requestConfig);
            CloseableHttpResponse response =null;
            try {
                response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                check.setOutput("GET " + checkUrl +" " + response.getStatusLine());
                if(entity != null){
                    String json = EntityUtils.toString(entity);
//                    ActuatorHealthVO actuatorHealthVO = gson.fromJson(json, ActuatorHealthVO.class);
                    Health health = gson.fromJson(json, Health.class);
                    if(health.getStatus().getCode().equals(Status.UP.toString())){
                        check.setOutput("HTTP GET " + checkUrl + " "+ response.getStatusLine()).setStatus("passing");
                        checkMapper.updateByPrimaryKey(check);
                    }
                }
                log.info("[Service] check pass "+check);
            } catch (Exception e) {
                check.setStatus("failing").setOutput("HTTP GET " + checkUrl + " "+ "refused");
                checkMapper.updateByPrimaryKey(check);
                log.warn("【服务异常】" + check);
            }
        },0,interval,timeUnit);
    }

    /**
     * 启动HTTP健康检查
     * @param checkId   检查Id
     * @param checkUrl  检查Url
     * @param interval  循环检查时间，带单位的字符串
     * @param timeout   超时时间，带单位的字符串
     */
    public void startHttpCheck(String checkId,String checkUrl,String interval,String timeout){
        long interval_num = TimeUtil.getTimeNum(interval);
        TimeUnit interval_unit = TimeUtil.getTimeUnit(interval);
        long timeout_num = TimeUtil.getTimeNum(timeout);
        TimeUnit timeout_unit = TimeUtil.getTimeUnit(timeout);
        long timeout_ms = TimeUnit.MILLISECONDS.convert(timeout_num, timeout_unit);
        startHttpCheck(checkId,checkUrl,interval_num,timeout_ms,interval_unit);
    }

    /**
     * 为新注册的服务开启TCP检查
     * @param newService    新注册的服务
     */
    public void startTcpCheck(NewService newService) {
        NewService.Check newServiceCheck = newService.getCheck();
        String url = newServiceCheck.getTcp();
        log.info("[Check] Start TCP Check : "+url);
        String interval = newServiceCheck.getInterval();    //循环时间
        String timeout = newServiceCheck.getTimeout();  //超时时间
        long interval_timeNum = TimeUtil.getTimeNum(interval);   //循环时间数字部分
        TimeUnit interval_timeUnit = TimeUtil.getTimeUnit(interval);    //循环时间单位
        long timeout_timeNum = TimeUtil.getTimeNum(timeout); //超时时间数字部分
        TimeUnit timeout_timeUnit = TimeUtil.getTimeUnit(timeout);  //超时时间单位
        Check check = new Check().setCheckId("service:"+newService.getId())
                .setName("Service '"+newService.getName()+"' check")
                .setServiceId(newService.getId())
                .setServiceName(newService.getName())
                .setNode(nodeName);

        //Check信息持久化，持久化url，interval,timeout等等到数据库
        CheckInfo checkInfo = new CheckInfo();
        checkInfo.setCheckId(check.getCheckId()).setInterval(newServiceCheck.getInterval()).setKind("tcp").setNode(nodeName).setTimeout(newServiceCheck.getTimeout()).setUrl(url);
        //检查checkInfo是否已经存在
        if(checkInfoMapper.selectByPrimaryKey(checkInfo.getCheckId())==null)    checkInfoMapper.insertSelective(checkInfo);
        //检查check是否已经存在
        if(checkMapper.selectByPrimaryKey(check.getCheckId())==null)    checkMapper.insertSelective(check);
        int oldValue = 1;    //设定check初始状态为1，代表passing
        int newValue = 1;
        startTcpCheck(check.getCheckId(),url,interval,timeout);
    }
}
