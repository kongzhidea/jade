package net.paoding.rose.jade.context.spring.scan;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.dataaccess.DataAccessFactory;
import net.paoding.rose.jade.rowmapper.RowMapperFactory;
import net.paoding.rose.jade.statement.InterpreterFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;

import static org.springframework.util.Assert.notNull;

public class JadeScannerConfigurer implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanNameAware {

    private String basePackage;

    private Class<? extends Annotation> annotationClass = DAO.class;

    private ApplicationContext applicationContext;

    // DataAccessFactoryAdapter， 需要传DataSourceFactory，可以选 SimpleDataSourceFactory 或者 SpringDataSourceFactory
    protected DataAccessFactory dataAccessFactory;

    // DefaultRowMapperFactory
    protected RowMapperFactory rowMapperFactory;

    // SpringInterpreterFactory，会自动带上DefaultInterpreterFactory，还可以配置自己的 interpreter
    protected InterpreterFactory interpreterFactory;

    private String beanName;

    /**
     * This property lets you set the base package for your mapper interface files.
     * <p/>
     * You can set more than one package by using a semicolon or comma as a separator.
     * <p/>
     * Mappers will be searched for recursively starting in the specified package(s).
     *
     * @param basePackage base package name
     */
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * {@inheritDoc}
     */
    public void setBeanName(String name) {
        this.beanName = name;
    }


    public void setDataAccessFactory(DataAccessFactory dataAccessFactory) {
        this.dataAccessFactory = dataAccessFactory;
    }

    public void setRowMapperFactory(RowMapperFactory rowMapperFactory) {
        this.rowMapperFactory = rowMapperFactory;
    }

    public void setInterpreterFactory(InterpreterFactory interpreterFactory) {
        this.interpreterFactory = interpreterFactory;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        notNull(this.basePackage, "Property 'basePackage' is required");
    }

    /**
     * {@inheritDoc}
     */
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    /**
     * {@inheritDoc}
     */
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(beanDefinitionRegistry);
        scanner.setBasePackage(this.basePackage);
        scanner.setAnnotationClass(this.annotationClass);
        scanner.setApplicationContext(this.applicationContext);
        scanner.setDataAccessFactory(getBeanByDefault(this.dataAccessFactory, "jade.dataAccessFactory"));
        scanner.setRowMapperFactory(getBeanByDefault(this.rowMapperFactory, "jade.rowMapperFactory"));
        scanner.setInterpreterFactory(getBeanByDefault(this.interpreterFactory, "jade.interpreterFactory"));
        scanner.registerFilters();

        scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    private <T> T getBeanByDefault(T bean, String beanName) {
        return bean != null ? bean :
                (T) this.applicationContext.getBean(beanName);
    }
}
