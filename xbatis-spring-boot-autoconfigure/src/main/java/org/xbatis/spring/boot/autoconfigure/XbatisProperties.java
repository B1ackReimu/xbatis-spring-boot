package org.xbatis.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "xbatis.datasource")
public class XbatisProperties {

    private String[] groupNames;



}
