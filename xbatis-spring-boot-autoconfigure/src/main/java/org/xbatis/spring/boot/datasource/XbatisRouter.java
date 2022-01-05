package org.xbatis.spring.boot.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.xbatis.spring.boot.annotation.Master;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;

public class XbatisRouter extends AbstractRoutingDataSource {

    private final ConcurrentHashMap<String, XbatisDataSourceConfig.XbatisNamespace> classShardAlgorithmMap;
    private final CopyOnWriteArraySet<String> methodIsMasterSet;

    public XbatisRouter(ConcurrentHashMap<String, XbatisDataSourceConfig.XbatisNamespace> classShardAlgorithmMap, CopyOnWriteArraySet<String> methodIsMasterSet) {
        this.classShardAlgorithmMap = classShardAlgorithmMap;
        this.methodIsMasterSet = methodIsMasterSet;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceSelector.getLocalClass();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        String lookupKey = (String) determineCurrentLookupKey();
        XbatisDataSourceConfig.XbatisNamespace.XbatisGroup group = classShardAlgorithmMap.get(lookupKey)
                .getGroup(DataSourceSelector.getLocalShareValue());
        return randomDataSource(methodIsMasterSet.contains(lookupKey), group);
    }

    @Override
    public void afterPropertiesSet() {
    }

    private DataSource randomDataSource(boolean isMaster, XbatisDataSourceConfig.XbatisNamespace.XbatisGroup group) {
        ArrayList<XbatisDataSourceConfig.XbatisNamespace.XbatisGroup.XbatisDatabase> databases;
        if (isMaster) {
            databases = group.getMasters();
        } else {
            databases = group.getSlaves();
        }
        return databases.get(ThreadLocalRandom.current().nextInt(databases.size())).dataSource;
    }
}
