package org.xbatis.spring.boot.autoconfigure;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xbatis.spring.boot.autoconfigure.annotation.Master;
import org.xbatis.spring.boot.autoconfigure.annotation.NameSpace;
import org.xbatis.spring.boot.autoconfigure.util.SqlRewriter;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ConditionalOnClass(XbatisDataSourceConfig.class)
@Configuration
@EnableConfigurationProperties({MybatisProperties.class})
public class XbatisAutoConfiguration {

    private final MybatisProperties properties;
    private final XbatisDataSourceConfig dataSourceConfig;
    private SqlSessionFactory sqlSessionFactory;
    private XbatisRouter xbatisRouter;

    public XbatisAutoConfiguration(MybatisProperties properties, XbatisDataSourceConfig dataSourceConfig) {
        this.properties = properties;
        this.dataSourceConfig = dataSourceConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public XbatisRouter xbatisRouter() {
        xbatisRouter = new XbatisRouter();
        return xbatisRouter;
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(XbatisRouter xbatisRouter) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(xbatisRouter);
        factoryBean.setMapperLocations(properties.resolveMapperLocations());
        factoryBean.afterPropertiesSet();
        sqlSessionFactory = factoryBean.getObject();
        doSetRouter();
        return sqlSessionFactory;
    }

    public void doSetRouter() throws Exception {
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
                    } else {
                        throw new Exception("there is not namespace in XbatisDataSourceConfig: " + value);
                    }
                    for (Method method : aClass.getMethods()) {
                        String methodFullName = className + "." + method.getName();
                        if (method.isAnnotationPresent(Master.class)) {
                            methodIsMasterSet.add(methodFullName);
                        }
                    }
                } else {
                    throw new Exception("there is not namespace on class: " + className);
                }
            }
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
            if (Objects.equals(sqlCommandType, SqlCommandType.INSERT) ||
                    Objects.equals(sqlCommandType, SqlCommandType.DELETE) ||
                    Objects.equals(sqlCommandType, SqlCommandType.UPDATE)) {
                methodIsMasterSet.add(statementId);
            }

        }
        xbatisRouter.setClassShardAlgorithmMap(classShardAlgorithmMap);
        xbatisRouter.setMethodIsMasterSet(methodIsMasterSet);
        sqlSessionFactory.getConfiguration().addInterceptor(new XbatisInterceptor(new SqlRewriter(xbatisRouter)));
    }


}
