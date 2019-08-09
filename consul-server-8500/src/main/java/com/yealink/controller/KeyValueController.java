package com.yealink.controller;

import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.KeyValueConsulClient;
import com.ecwid.consul.v1.kv.model.GetBinaryValue;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.yealink.service.KeyValueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author happy-xjb
 * @date 2019/8/7 10:42
 */
@Slf4j
@RestController
@RequestMapping("/v1/kv")
public class KeyValueController {
    @Autowired
    KeyValueService keyValueService;

    @GetMapping("/**")
    public List<GetBinaryValue> getKey(HttpServletRequest request){
        String requestURI = request.getRequestURI();
        String remoteAddr = request.getRemoteAddr();
        int remotePort = request.getRemotePort();
        log.info("http: Request GET "+requestURI+" from="+remoteAddr+":"+remotePort);
        //获得key
        String key = requestURI.substring(7);
        return keyValueService.getBinaryKeyValue(key);
    }

    @PutMapping("/**")
    public boolean addKey(HttpServletRequest request, @RequestBody String value){
        String requestURI = request.getRequestURI();
        String remoteAddr = request.getRemoteAddr();
        int remotePort = request.getRemotePort();
        log.info("http: Request PUT "+requestURI+" from="+remoteAddr+":"+remotePort);
        //获得key
        String key = requestURI.substring(7);
        keyValueService.setKeyValue(key,value);
        return true;
    }

    @DeleteMapping("/**")
    public boolean deleteKVValue(HttpServletRequest request){
        String requestURI = request.getRequestURI();
        String remoteAddr = request.getRemoteAddr();
        int remotePort = request.getRemotePort();
        log.info("http: Request DELETE "+requestURI+" from="+remoteAddr+":"+remotePort);
        //获得key
        String key = requestURI.substring(7);
        keyValueService.deleteKeyValue(key);
        return true;
    }



}
