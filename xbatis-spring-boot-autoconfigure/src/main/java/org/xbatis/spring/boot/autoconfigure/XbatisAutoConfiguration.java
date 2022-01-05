package org.xbatis.spring.boot.autoconfigure;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xbatis.spring.boot.annotation.Master;
import org.xbatis.spring.boot.annotation.NameSpace;
import org.xbatis.spring.boot.datasource.XbatisDataSourceConfig;
import org.xbatis.spring.boot.datasource.XbatisRouter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ConditionalOnClass(XbatisDataSourceConfig.class)
@Configuration
@EnableConfigurationProperties({MybatisProperties.class})
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class XbatisAutoConfiguration {

    private final MybatisProperties properties;
    private final XbatisDataSourceConfig dataSourceConfig;

    public XbatisAutoConfiguration(MybatisProperties properties, XbatisDataSourceConfig dataSourceConfig) {
        this.properties = properties;
        this.dataSourceConfig = dataSourceConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public XbatisRouter xbatisRouter(SqlSessionFactory sqlSessionFactory) throws ClassNotFoundException {
        ConcurrentHashMap<String, XbatisDataSourceConfig.XbatisNamespace> classShardAlgorithmMap = new ConcurrentHashMap<>();
        CopyOnWriteArraySet<String> methodIsMasterSet = new CopyOnWriteArraySet<>();
        Collection<MappedStatement> mappedStatements = sqlSessionFactory.getConfiguration().getMappedStatements();
        HashMap<String, Class<?>> classMap = new HashMap<>();
        for (MappedStatement mappedStatement : mappedStatements) {
            String statementId = mappedStatement.getId();
            String className = statementId.substring(0, statementId.lastIndexOf("."));
            Class<?> aClass = classMap.get(className);
            if (Objects.isNull(aClass)) {
                aClass = Class.forName(className);
                if (aClass.isAnnotationPresent(NameSpace.class)) {
                    String value = aClass.getAnnotation(NameSpace.class).value();
                    XbatisDataSourceConfig.XbatisNamespace namespace = dataSourceConfig.getNamespace(value);
                    if (Objects.nonNull(namespace)) {
                        classShardAlgorithmMap.put(className, namespace);
                        classMap.put(className, aClass);
                    }
                } else {
                    continue;
                }
            }
            for (Method method : aClass.getMethods()) {
                String methodFullName = className + method.getName();
                if (Objects.equals(methodFullName, className)) {
                    if (method.isAnnotationPresent(Master.class)) {
                        methodIsMasterSet.add(methodFullName);
                    }
                }
            }
        }
        return new XbatisRouter(classShardAlgorithmMap, methodIsMasterSet);
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(XbatisRouter xbatisRouter) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(xbatisRouter);
        factoryBean.setMapperLocations(properties.resolveMapperLocations());
        return factoryBean.getObject();
    }
}
