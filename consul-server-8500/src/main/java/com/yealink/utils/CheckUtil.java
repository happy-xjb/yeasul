package com.yealink.utils;

import com.ecwid.consul.v1.agent.model.NewService;
import com.google.gson.Gson;
import com.yealink.dao.CheckInfoMapper;
import com.yealink.dao.CheckMapper;
import com.yealink.entities.Check;
import com.yealink.entities.CheckInfo;
import com.yealink.service.AgentService;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CheckUtil {
    @Value("${consul.config.node-name}")
    String nodeName;
    @Autowired
    AgentService agentService;

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

    //用来记录倒计时一直为critical的时间，如果一直保持critical，则注销服务
    public static ConcurrentHashMap<String,Long> criticalCountMap = new ConcurrentHashMap<>();

    //用来记录健康检查的scheduledFuture
    public static ConcurrentHashMap<String,ScheduledFuture> scheduledFutureMap = new ConcurrentHashMap<>();

    /**
     * 为新注册的服务开启检查
     * @param newService    新注册的服务
     */
    public void startHttpCheck(NewService newService){
        System.out.println("进入了HTTPCHECK");
        NewService.Check newServiceCheck = newService.getCheck();
        String url = newServiceCheck.getHttp();
        log.info("[Check] Start HTTP Check : "+url);
        String interval = newServiceCheck.getInterval();    //循环时间
        StringBuffer timeoutSb = null;
        //如果timeout没有设置，那就让timeout和interval相等
        if(newServiceCheck.getTimeout()==null||newServiceCheck.getTimeout().equals("")) timeoutSb = new StringBuffer(interval);
        else timeoutSb = new StringBuffer(newServiceCheck.getTimeout());
        String timeout = timeoutSb.toString();  //超时时间
        long interval_timeNum = TimeUtil.getTimeNum(interval);   //循环时间数字部分
        TimeUnit interval_timeUnit = TimeUtil.getTimeUnit(interval);    //循环时间单位
        long timeout_timeNum = TimeUtil.getTimeNum(timeout); //超时时间数字部分
        TimeUnit timeout_timeUnit = TimeUtil.getTimeUnit(timeout);  //超时时间单位
        Check check = new Check().setCheckId("service:"+newService.getId())
                .setName("Service '"+newService.getName()+"' check")
                .setServiceId(newService.getId())
                .setServiceName(newService.getName())
                .setNode(nodeName);

        if(newServiceCheck.getStatus()!=null&&!newServiceCheck.getStatus().equals(""))    check.setStatus(newServiceCheck.getStatus());
        else check.setStatus("critical");

        //Check信息持久化，持久化url，interval,timeout等等到数据库
        CheckInfo checkInfo = new CheckInfo();
        checkInfo.setCheckId(check.getCheckId()).setInterval(interval).setKind("http").setNode(nodeName).setTimeout(timeout).setUrl(url);
        //检查checkInfo是否已经存在
        if(checkInfoMapper.selectByPrimaryKey(checkInfo.getCheckId())==null)    checkInfoMapper.insertSelective(checkInfo);
        //检查check是否已经存在
        if(checkMapper.selectByPrimaryKey(check.getCheckId())==null)    checkMapper.insertSelective(check);

        String criticalTime = newServiceCheck.getDeregisterCriticalServiceAfter();
        long criticalMills = getCriticalMills(criticalTime);
        long count = criticalMills;
        if(criticalTime!=null&&!criticalTime.equals(""))    criticalCountMap.put(newService.getId(),criticalMills);

        //先把上次的任务取消
        ScheduledFuture currentScheduledFuture = scheduledFutureMap.get(check.getCheckId());
        if(currentScheduledFuture!=null){
            currentScheduledFuture.cancel(true);
        }

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
                criticalCountMap.put(newService.getId(),criticalMills);
                log.info("[Service] check pass "+check);
            } catch (Exception e) {
                check.setStatus("critical").setOutput("HTTP GET " + url + " "+ "refused");
                checkMapper.updateByPrimaryKey(check);
                log.warn("【服务异常】" + check);
                //TODO 开启检查critical,如果在deregisterCriticalServiceAfter时间后仍为critical，则注销服务
                if(criticalTime!=null&&!criticalTime.equals("")){
                    //判断时间倒计时到了没
                    Long timeRest = criticalCountMap.get(newService.getId());
                    System.out.println("还剩下"+timeRest);
                    if(timeRest>0){
                        timeRest-=TimeUnit.MILLISECONDS.convert(interval_timeNum,interval_timeUnit);
                        criticalCountMap.put(newService.getId(),timeRest);
                    }else {
                        //取消检查任务
                        ScheduledFuture currentTask = scheduledFutureMap.get(check.getCheckId());
                        if(currentTask!=null)   currentTask.cancel(true);
                        agentService.agentServiceDeregister(newService.getId());
                    }
                }
            }
        }, 0, interval_timeNum, interval_timeUnit);

        //放入futureMap
        scheduledFutureMap.put(check.getCheckId(),scheduledFuture);

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
                //  将check状态变成passing,更新output
                checkMapper.updateStatusToPassingByPrimaryKey(checkId);
                checkMapper.updateOutputByPrimaryKey(checkId,"socket connection success "+host+":"+port);
            } catch (IOException e) {
                log.warn("agent: socket connection failed '"+host+":"+port);
                // 将check状态变成failing，更新output
                checkMapper.updateStatusToCriticalByPrimaryKey(checkId);
                checkMapper.updateOutputByPrimaryKey(checkId,"socket connection failed "+host+":"+port);
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
        //先把上次的任务取消
        ScheduledFuture currentScheduledFuture = scheduledFutureMap.get(checkId);
        if(currentScheduledFuture!=null){
            currentScheduledFuture.cancel(true);
        }
        //开启定时任务
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout((int) timeout).setConnectTimeout((int) timeout).build();
            HttpGet httpGet = new HttpGet(checkUrl);
            httpGet.setConfig(requestConfig);
            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                check.setOutput("GET " + checkUrl + " " + response.getStatusLine());
                if (entity != null) {
                    String json = EntityUtils.toString(entity);
//                    ActuatorHealthVO actuatorHealthVO = gson.fromJson(json, ActuatorHealthVO.class);
                    Health health = gson.fromJson(json, Health.class);
                    if (health.getStatus().getCode().equals(Status.UP.toString())) {
                        check.setOutput("HTTP GET " + checkUrl + " " + response.getStatusLine()).setStatus("passing");
                        checkMapper.updateByPrimaryKey(check);
                    }
                }
                log.info("[Service] check pass " + check);
            } catch (Exception e) {
                check.setStatus("critical").setOutput("HTTP GET " + checkUrl + " " + "refused");
                checkMapper.updateByPrimaryKey(check);
                log.warn("【服务异常】" + check);
            }
        }, 0, interval, timeUnit);

        scheduledFutureMap.put(checkId,scheduledFuture);
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
        StringBuffer timeoutSb = null;
        //如果timeout没有设置，那就让timeout和interval相等
        if(newServiceCheck.getTimeout()==null||newServiceCheck.getTimeout().equals("")) timeoutSb = new StringBuffer(interval);
        else timeoutSb = new StringBuffer(newServiceCheck.getTimeout());


        String timeout = timeoutSb.toString();  //超时时间
        long interval_timeNum = TimeUtil.getTimeNum(interval);   //循环时间数字部分
        TimeUnit interval_timeUnit = TimeUtil.getTimeUnit(interval);    //循环时间单位
        long timeout_timeNum = TimeUtil.getTimeNum(timeout); //超时时间数字部分
        TimeUnit timeout_timeUnit = TimeUtil.getTimeUnit(timeout);  //超时时间单位
        Check check = new Check().setCheckId("service:"+newService.getId())
                .setName("Service '"+newService.getName()+"' check")
                .setServiceId(newService.getId())
                .setServiceName(newService.getName())
                .setNode(nodeName);

        if(newServiceCheck.getStatus()!=null&&!newServiceCheck.getStatus().equals(""))    check.setStatus(newServiceCheck.getStatus());
        else check.setStatus("critical");

        //Check信息持久化，持久化url，interval,timeout等等到数据库
        CheckInfo checkInfo = new CheckInfo();
        checkInfo.setCheckId(check.getCheckId()).setInterval(interval).setKind("tcp").setNode(nodeName).setTimeout(timeout).setUrl(url);
        //检查checkInfo是否已经存在
        if(checkInfoMapper.selectByPrimaryKey(checkInfo.getCheckId())==null)    checkInfoMapper.insertSelective(checkInfo);
        //检查check是否已经存在
        if(checkMapper.selectByPrimaryKey(check.getCheckId())==null)    checkMapper.insertSelective(check);
        int oldValue = 1;    //设定check初始状态为1，代表passing
        int newValue = 1;
        startTcpCheck(check.getCheckId(),url,interval,timeout);
    }

    private long getCriticalMills(String criticalTime) {
        if(criticalTime==null||criticalTime.equals("")) return 0;
        long timeNum = TimeUtil.getTimeNum(criticalTime);
        TimeUnit timeUnit = TimeUtil.getTimeUnit(criticalTime);
        return TimeUnit.MILLISECONDS.convert(timeNum,timeUnit);
    }

}


