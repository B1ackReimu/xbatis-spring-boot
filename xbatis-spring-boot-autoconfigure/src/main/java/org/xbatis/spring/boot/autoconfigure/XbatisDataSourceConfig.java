package org.xbatis.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.sql.DataSource;
import java.util.*;

public final class XbatisDataSourceConfig {

    private final HashSet<XbatisNamespace> namespaces = new HashSet<>();
    private final ClassLoader classLoader;

    public XbatisNamespace addNamespace(String name, Class<? extends DataSource> type, String driverClassName, ShardAlgorithm groupShardAlgorithm, String[] groupShardKey) {
        XbatisNamespace xbatisNamespace = new XbatisNamespace(name, type, driverClassName, groupShardAlgorithm, groupShardKey);
        namespaces.add(xbatisNamespace);
        return xbatisNamespace;
    }

    public XbatisNamespace getNamespace(String spaceName) {
        for (XbatisNamespace namespace : namespaces) {
            if (Objects.equals(spaceName, namespace.spaceName)) {
                return namespace;
            }
        }
        return null;
    }

    public XbatisDataSourceConfig(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public class XbatisNamespace {
        private final String spaceName;
        private final Class<? extends DataSource> type;
        private final String driverClassName;
        private final HashMap<String, XbatisGroup> groups = new HashMap<>();
        private final HashMap<String, XbatisTable> tables = new HashMap<>();
        private final ShardAlgorithm groupShardAlgorithm;
        private final String[] groupShardKeys;

        public XbatisNamespace(String spaceName, Class<? extends DataSource> type, String driverClassName, ShardAlgorithm groupShardAlgorithm, String[] groupShardKeys) {
            this.spaceName = spaceName;
            this.type = type;
            this.driverClassName = driverClassName;
            this.groupShardAlgorithm = groupShardAlgorithm;
            this.groupShardKeys = groupShardKeys;
        }

        public XbatisGroup addGroup(String groupSuffix) {
            XbatisGroup xbatisGroup = new XbatisGroup(groupSuffix);
            groups.put(groupSuffix, xbatisGroup);
            return xbatisGroup;
        }

        public void addTable(String tableName, ShardAlgorithm algorithmClass, String[] shardKey) {
            tables.put(tableName,new XbatisTable(tableName, algorithmClass, shardKey));
        }

        public XbatisTable getTable(String tableName){
            return tables.get(tableName);
        }

        protected XbatisGroup getGroup(Map<String,Object> args) {
            return groups.get(groupShardAlgorithm.shardSuffix(args));
        }

        protected String[] getGroupShardKeys() {
            return groupShardKeys;
        }

        public class XbatisTable {
            String tableName;
            ShardAlgorithm tableShardAlgorithm;
            String[] shardKey;

            public XbatisTable(String tableName, ShardAlgorithm tableShardAlgorithm, String[] shardKey) {
                this.tableName = tableName;
                this.tableShardAlgorithm = tableShardAlgorithm;
                this.shardKey = shardKey;
            }

            public String getTableSuffix(Map<String,Object> args){
                return tableShardAlgorithm.shardSuffix(args);
            }

        }

        public class XbatisGroup {
            String groupSuffix;
            ArrayList<XbatisDatabase> masters = new ArrayList<>();
            ArrayList<XbatisDatabase> slaves = new ArrayList<>();

            public XbatisGroup(String groupSuffix) {
                this.groupSuffix = groupSuffix;
            }

            public XbatisGroup addMaster(String url, String username, String password) {
                masters.add(new XbatisDatabase(url, username, password));
                return this;
            }

            public XbatisGroup addSlave(String url, String username, String password) {
                slaves.add(new XbatisDatabase(url, username, password));
                return this;
            }

            protected ArrayList<XbatisDatabase> getMasters() {
                return masters;
            }

            protected ArrayList<XbatisDatabase> getSlaves() {
                return slaves;
            }

            class XbatisDatabase {

                final String url;
                final String username;
                final String password;
                DataSource dataSource;

                public XbatisDatabase(String url, String username, String password) {
                    this.url = url;
                    this.username = username;
                    this.password = password;
                    createDataSource();
                }

                private void createDataSource() {
                    DataSourceProperties dataSourceProperties = new DataSourceProperties();
                    dataSourceProperties.setPassword(password);
                    dataSourceProperties.setUsername(username);
                    dataSourceProperties.setUrl(url);
                    dataSourceProperties.setDriverClassName(XbatisNamespace.this.driverClassName);
                    dataSourceProperties.setType(XbatisNamespace.this.type);
                    dataSourceProperties.setBeanClassLoader(XbatisDataSourceConfig.this.classLoader);
                    this.dataSource = dataSourceProperties.initializeDataSourceBuilder().build();
                    try {
                        dataSourceProperties.afterPropertiesSet();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}