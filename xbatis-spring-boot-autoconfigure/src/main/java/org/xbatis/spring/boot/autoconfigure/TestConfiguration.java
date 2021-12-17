package org.xbatis.spring.boot.autoconfigure;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(A.class)
public class TestConfiguration implements ApplicationContextAware {

    public TestConfiguration(@Qualifier(value = "b") A a) {
        System.out.println("TestConfiguration:" + a.getClassName());
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context.getBeanFactory();
        beanDefinitionRegistry.removeBeanDefinition("b");
    }
}

