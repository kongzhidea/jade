package net.paoding.rose.jade.context.spring;

import java.util.Map;

import net.paoding.rose.jade.dataaccess.DataSourceFactory;
import net.paoding.rose.jade.dataaccess.DataSourceHolder;
import net.paoding.rose.jade.statement.StatementMetaData;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * SpringDataSourceFactory 代理模式， 如果没有配置  jade.dataSourceFactory，则默认为 SpringDataSourceFactory
 *
 * @author 王志亮 [qieqie.wang@gmail.com]
 */
public class SpringDataSourceFactoryDelegate implements DataSourceFactory, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private DataSourceFactory dataSourceFactory;


    @Override
    public DataSourceHolder getHolder(StatementMetaData metaData, Map<String, Object> runtimeProperties) {
        if (dataSourceFactory == null) {
            if (applicationContext != null) {
                if (applicationContext.containsBeanDefinition("jade.dataSourceFactory")) {
                    dataSourceFactory = applicationContext.getBean(
                            "jade.dataSourceFactory", DataSourceFactory.class);
                } else {
                    dataSourceFactory = new SpringDataSourceFactory(applicationContext);
                }
                this.applicationContext = null;
            }
        }
        return dataSourceFactory.getHolder(metaData, runtimeProperties);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
