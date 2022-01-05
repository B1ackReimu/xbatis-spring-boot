package org.xbatis.spring.boot.datasource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.xbatis.spring.boot.ShardAlgorithm;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

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
        private String spaceName;
        private Class<? extends DataSource> type;
        private final String driverClassName;
        private final HashSet<XbatisGroup> groups = new HashSet<>();
        private final HashSet<XbatisTable> tables = new HashSet<>();
        private ShardAlgorithm groupShardAlgorithm;
        private String[] groupShardKeys;

        public XbatisNamespace(String spaceName, Class<? extends DataSource> type, String driverClassName, ShardAlgorithm groupShardAlgorithm, String[] groupShardKeys) {
            this.spaceName = spaceName;
            this.type = type;
            this.driverClassName = driverClassName;
            this.groupShardAlgorithm = groupShardAlgorithm;
            this.groupShardKeys = groupShardKeys;
        }

        public XbatisGroup addGroup(String groupSuffix) {
            XbatisGroup xbatisGroup = new XbatisGroup(groupSuffix);
            groups.add(xbatisGroup);
            return xbatisGroup;
        }

        public void addTable(String tableName, ShardAlgorithm algorithmClass, String[] shardKey) {
            tables.add(new XbatisTable(tableName, algorithmClass, shardKey));
        }

        protected XbatisGroup getGroup(String... shardValue) {
            for (XbatisGroup group : groups) {
                if (Objects.equals(groupShardAlgorithm.shardSuffix(shardValue), group.groupSuffix)) {
                    return group;
                }
            }
            return null;
        }

        protected String[] getGroupShardKeys() {
            return groupShardKeys;
        }

        private class XbatisTable {
            String tableName;
            ShardAlgorithm algorithmClass;
            String[] shardKey;

            public XbatisTable(String tableName, ShardAlgorithm algorithmClass, String[] shardKey) {
                this.tableName = tableName;
                this.algorithmClass = algorithmClass;
                this.shardKey = shardKey;
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