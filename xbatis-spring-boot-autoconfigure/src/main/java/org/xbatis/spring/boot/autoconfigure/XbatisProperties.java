package org.xbatis.spring.boot.autoconfigure;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.xbatis.spring.boot.ShardAlgorithm;

import java.util.HashMap;

@ConfigurationProperties(prefix = XbatisProperties.PREFIX)
public class XbatisProperties implements EnvironmentAware {

    public final static String PREFIX = "xbatis";

    private HashMap<String, ShardAlgorithm> algorithmHashMap = new HashMap<>();

    @Bean("dbRouter")
    HashMap<String,HashMap<String,XDBRouter>> dbRouter(){
        // <namespace,<group,db>>
        HashMap<String,HashMap<String,XDBRouter>> dbRouter = new HashMap<>();
        for (String namespace : namespaces) {
            System.out.println(namespace);
            String property = environment.getProperty(PREFIX + "." + namespace + "." + "group");
            System.out.println(property);
        }
        return dbRouter;
    }

    private String[] namespaces;

    public String[] getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(String[] namespaces) {
        this.namespaces = namespaces;
    }

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
