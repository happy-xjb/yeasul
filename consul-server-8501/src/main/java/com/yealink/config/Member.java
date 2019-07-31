package com.yealink.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class Member {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Addr")
    private String address;

    @JsonProperty("Port")
    private Integer port;

    @JsonProperty("Tags")
    private Map<String, String> tags;

    @JsonProperty("Status")
    private int status;

    @JsonProperty("ProtocolMin")
    private int protocolMin;

    @JsonProperty("ProtocolMax")
    private int protocolMax;

    @JsonProperty("ProtocolCur")
    private int protocolCur;

    @JsonProperty("DelegateMin")
    private int delegateMin;

    @JsonProperty("DelegateMax")
    private int delegateMax;

    @JsonProperty("DelegateCur")
    private int delegateCur;

    
}
