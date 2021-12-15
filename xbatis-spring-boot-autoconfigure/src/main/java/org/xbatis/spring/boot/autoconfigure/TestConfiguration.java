package org.xbatis.spring.boot.autoconfigure;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;

import javax.sql.DataSource;

public class TestConfiguration {

    public SqlSessionFactoryBean sqlSessionFactoryBean(){
        MybatisAutoConfiguration mybatisAutoConfiguration = new MybatisAutoConfiguration();
    }

}
