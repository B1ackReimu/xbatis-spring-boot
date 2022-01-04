package org.xbatis.spring.boot.config;

import org.apache.ibatis.annotations.Param;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.xbatis.spring.boot.annotation.Master;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class XbatisRouter extends AbstractRoutingDataSource {

    private ConcurrentHashMap<Method, XbatisDataSource.XbatisNamespace> methodShardAlgorithmMap;
    private ConcurrentHashMap<Method, Annotation> methodAnnotationMap;
    private ThreadLocal<Method> localMethod;
    private ThreadLocal<String[]> localShardValue;

    public XbatisRouter(XbatisDataSource xbatisDataSource) {

    }

    @Override
    protected Object determineCurrentLookupKey() {
        return localMethod.get();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        Method lookupKey = (Method) determineCurrentLookupKey();
        XbatisDataSource.XbatisNamespace.XbatisGroup group = methodShardAlgorithmMap.get(lookupKey)
                .getGroup(localShardValue.get());
        return randomDataSource(methodAnnotationMap.get(lookupKey) instanceof Master, group);
    }

    @Override
    public void afterPropertiesSet() {
    }

    private DataSource randomDataSource(boolean isMaster, XbatisDataSource.XbatisNamespace.XbatisGroup group) {
        ArrayList<XbatisDataSource.XbatisNamespace.XbatisGroup.XbatisDatabase> databases;
        if (isMaster) {
            databases = group.getMasters();
        } else {
            databases = group.getSlaves();
        }
        return databases.get(ThreadLocalRandom.current().nextInt(databases.size())).dataSource;
    }
}
