package org.xbatis.spring.boot.autoconfigure;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.xbatis.spring.boot.autoconfigure.DataSourceSelector;
import org.xbatis.spring.boot.autoconfigure.XbatisDataSourceConfig;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;

public class XbatisRouter extends AbstractRoutingDataSource {

    private ConcurrentHashMap<String, XbatisDataSourceConfig.XbatisNamespace> classShardAlgorithmMap;
    private CopyOnWriteArraySet<String> methodIsMasterSet;

    public void setClassShardAlgorithmMap(ConcurrentHashMap<String, XbatisDataSourceConfig.XbatisNamespace> classShardAlgorithmMap) {
        this.classShardAlgorithmMap = classShardAlgorithmMap;
    }

    public void setMethodIsMasterSet(CopyOnWriteArraySet<String> methodIsMasterSet) {
        this.methodIsMasterSet = methodIsMasterSet;
    }

    @Override
    public void afterPropertiesSet() {
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
        return randomDataSource(methodIsMasterSet.contains(DataSourceSelector.getLocalMethod()), group);
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
