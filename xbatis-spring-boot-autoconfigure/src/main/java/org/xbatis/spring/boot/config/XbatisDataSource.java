package org.xbatis.spring.boot.config;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.xbatis.spring.boot.ShardAlgorithm;

import javax.sql.DataSource;
import java.util.HashSet;

public final class XbatisDataSource {

    private final HashSet<XbatisNamespace> namespaces = new HashSet<>();
    private ClassLoader classLoader;

    public XbatisNamespace addNamespace(String name, Class<? extends DataSource> type, String driverClassName) {
        XbatisNamespace xbatisNamespace = new XbatisNamespace(name, type, driverClassName);
        namespaces.add(xbatisNamespace);
        return xbatisNamespace;
    }

    public XbatisDataSource(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public class XbatisNamespace {
        String name;
        Class<? extends DataSource> type;
        private final String driverClassName;
        HashSet<XbatisGroup> groups = new HashSet<>();
        HashSet<XbatisTable> tables = new HashSet<>();

        public XbatisNamespace(String name, Class<? extends DataSource> type, String driverClassName) {
            this.name = name;
            this.type = type;
            this.driverClassName = driverClassName;
        }

        public XbatisGroup addGroup(String name, ShardAlgorithm algorithmClass, String[] shardKey) {
            XbatisGroup xbatisGroup = new XbatisGroup(name, algorithmClass, shardKey);
            groups.add(xbatisGroup);
            return xbatisGroup;
        }

        public void addTable(String name, ShardAlgorithm algorithmClass, String[] shardKey) {
            tables.add(new XbatisTable(name, algorithmClass, shardKey));
        }

        private class XbatisTable {
            String name;
            ShardAlgorithm algorithmClass;
            String[] shardKey;

            public XbatisTable(String name, ShardAlgorithm algorithmClass, String[] shardKey) {
                this.name = name;
                this.algorithmClass = algorithmClass;
                this.shardKey = shardKey;
            }
        }

        public class XbatisGroup {
            String name;
            ShardAlgorithm algorithmClass;
            String[] shardKey;
            HashSet<XbatisDatabase> masters = new HashSet<>();
            HashSet<XbatisDatabase> slaves = new HashSet<>();

            public XbatisGroup(String name, ShardAlgorithm algorithmClass, String[] shardKey) {
                this.name = name;
                this.algorithmClass = algorithmClass;
                this.shardKey = shardKey;
            }

            public XbatisGroup addMaster(String url, String username, String password) {
                masters.add(new XbatisDatabase(url, username, password));
                return this;
            }

            public XbatisGroup addSlave(String url, String username, String password) {
                slaves.add(new XbatisDatabase(url, username, password));
                return this;
            }

            private class XbatisDatabase {

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

                private void createDataSource(){
                    DataSourceProperties dataSourceProperties = new DataSourceProperties();
                    dataSourceProperties.setPassword(password);
                    dataSourceProperties.setUsername(username);
                    dataSourceProperties.setUrl(url);
                    dataSourceProperties.setDriverClassName(XbatisNamespace.this.driverClassName);
                    dataSourceProperties.setType(XbatisNamespace.this.type);
                    dataSourceProperties.setBeanClassLoader(XbatisDataSource.this.classLoader);
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