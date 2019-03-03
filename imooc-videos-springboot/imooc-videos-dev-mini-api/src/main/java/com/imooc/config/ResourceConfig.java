package com.imooc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @Author: mate_J
 * @Date: 2019/2/17 13:32
 * @Version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "com.imooc")
@PropertySource("classpath:resource.properties")
public class ResourceConfig {
    private String zookeeperServer;
    private String bgmServer;
    private String fileSpace;

    public String getZookeeperServer() {
        return zookeeperServer;
    }

    public void setZookeeperServer(String zookeeperServer) {
        this.zookeeperServer = zookeeperServer;
    }

    public String getBgmServer() {
        return bgmServer;
    }

    public void setBgmServer(String bgmServer) {
        this.bgmServer = bgmServer;
    }

    public String getFileSpace() {
        return fileSpace;
    }

    public void setFileSpace(String fileSpace) {
        this.fileSpace = fileSpace;
    }
}
