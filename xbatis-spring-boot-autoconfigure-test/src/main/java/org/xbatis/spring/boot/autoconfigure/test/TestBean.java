package org.xbatis.spring.boot.autoconfigure.test;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xbatis.spring.boot.autoconfigure.A;
import org.xbatis.spring.boot.config.XbatisDataSource;

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
    public XbatisDataSource xbatisConfig(){
        XbatisDataSource xbatisDataSource = new XbatisDataSource(classLoader);
        XbatisDataSource.XbatisNamespace ns1 = xbatisDataSource.addNamespace("ns1", HikariDataSource.class, "com.mysql.cj.jdbc.Driver");
        ns1.addGroup("g1", new TestSA(),new String[]{"zxc"})
                .addMaster("192.168.44.1","zxc","asdas");
        return xbatisDataSource;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
