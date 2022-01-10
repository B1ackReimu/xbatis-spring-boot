package org.xbatis.spring.boot.autoconfigure.test;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xbatis.spring.boot.autoconfigure.XbatisDataSourceConfig;
import org.xbatis.spring.boot.autoconfigure.test.shardalgorithm.GroupSA;

@Configuration
public class TestBean implements BeanClassLoaderAware {

    private ClassLoader classLoader;

    @Bean
    public XbatisDataSourceConfig xbatisConfig() {
        XbatisDataSourceConfig xbatisDataSourceConfig = new XbatisDataSourceConfig(classLoader);
        XbatisDataSourceConfig.XbatisNamespace ns1 = xbatisDataSourceConfig.addNamespace("ns1", HikariDataSource.class, "com.mysql.cj.jdbc.Driver", new TestSA(), new String[]{"zxc"});
        ns1.addGroup("g1")
                .addMaster("jdbc:mysql://192.168.44.253/testdb", "root", "amdyes");
        GroupSA groupSA = new GroupSA();
        ns1.addTable("blog",groupSA,new String[]{"id"});
        return xbatisDataSourceConfig;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
