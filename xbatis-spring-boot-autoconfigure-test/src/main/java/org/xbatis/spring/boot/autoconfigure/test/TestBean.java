package org.xbatis.spring.boot.autoconfigure.test;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xbatis.spring.boot.autoconfigure.A;
import org.xbatis.spring.boot.datasource.XbatisDataSourceConfig;

@Configuration
public class TestBean implements BeanClassLoaderAware {

    private ClassLoader classLoader;

    @Bean
    public A a() {
        A a = new A();
        a.setClassName(A.class.getName());
        System.out.println("@Bean:" + a.getClassName());
        return a;
    }

    @Bean
    public A b() {
        A a = new A();
        a.setClassName("@Configuration:" + a.getClass().getName());
        return a;
    }

    @Bean
    public XbatisDataSourceConfig xbatisConfig() {
        XbatisDataSourceConfig xbatisDataSourceConfig = new XbatisDataSourceConfig(classLoader);
        XbatisDataSourceConfig.XbatisNamespace ns1 = xbatisDataSourceConfig.addNamespace("ns1", HikariDataSource.class, "com.mysql.cj.jdbc.Driver", new TestSA(), new String[]{"zxc"});
        ns1.addGroup("g1")
                .addMaster("192.168.44.1", "zxc", "asdas");
        return xbatisDataSourceConfig;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
