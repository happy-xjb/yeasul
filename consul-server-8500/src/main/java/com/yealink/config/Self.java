package com.yealink.config;

import com.ecwid.consul.v1.agent.model.Member;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.UUID;


@Data
@ConfigurationProperties(prefix = "consul")
public class Self {

    public enum LogLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERR
    }

//    @Data
    public static class Config {

        @JsonProperty("Datacenter")
        private String datacenter;

        @JsonProperty("NodeName")
        private String nodeName;

        @JsonProperty("Revision")
        private String revision;

        @JsonProperty("Server")
        private boolean server;

        @JsonProperty("Version")
        private String version;

    public String getDatacenter() {
        return datacenter;
    }

    public void setDatacenter(String datacenter) {
        this.datacenter = datacenter;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName)  {

        this.nodeName = nodeName;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public boolean isServer() {
        return server;
    }

    public void setServer(boolean server) {
        this.server = server;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

    @Data
    public static class DebugConfig {

        @JsonProperty("Bootstrap")
        private boolean bootstrap;

        @JsonProperty("DataDir")
        private String dataDir;

        @JsonProperty("DNSRecursor")
        private String dnsRecursor;

        @JsonProperty("DNSDomain")
        private String dnsDomain;

        @JsonProperty("LogLevel")
        private LogLevel logLevel;

        @JsonProperty("NodeID")
        private String nodeId = UUID.randomUUID().toString();

        @JsonProperty("ClientAddrs")
        private String[] clientAddresses;

        @JsonProperty("BindAddr")
        private String bindAddress;

        @JsonProperty("LeaveOnTerm")
        private boolean leaveOnTerm;

        @JsonProperty("SkipLeaveOnInt")
        private boolean skipLeaveOnInt;

        @JsonProperty("EnableDebug")
        private boolean enableDebug;

        @JsonProperty("VerifyIncoming")
        private boolean verifyIncoming;

        @JsonProperty("VerifyOutgoing")
        private boolean verifyOutgoing;

        @JsonProperty("CAFile")
        private String caFile;

        @JsonProperty("CertFile")
        private String certFile;

        @JsonProperty("KeyFile")
        private String keyFile;

        @JsonProperty("UiDir")
        private String uiDir;

        @JsonProperty("PidFile")
        private String pidFile;

        @JsonProperty("EnableSyslog")
        private boolean enableSyslog;

        @JsonProperty("RejoinAfterLeave")
        private boolean rejoinAfterLeave;


    }

    @JsonProperty("Config")
    private Config config = new Config();

    @JsonProperty("DebugConfig")
    private DebugConfig debugConfig = new DebugConfig();

    @JsonProperty("Member")
    private Member member;
}
