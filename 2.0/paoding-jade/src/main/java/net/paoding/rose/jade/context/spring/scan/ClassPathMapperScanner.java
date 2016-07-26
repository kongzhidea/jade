
package net.paoding.rose.jade.context.spring.scan;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.context.spring.JadeFactoryBean;
import net.paoding.rose.jade.dataaccess.DataAccessFactory;
import net.paoding.rose.jade.rowmapper.RowMapperFactory;
import net.paoding.rose.jade.statement.InterpreterFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Set;

public class ClassPathMapperScanner extends ClassPathBeanDefinitionScanner {
    private String basePackage;

    private Class<? extends Annotation> annotationClass = DAO.class;

    private ApplicationContext applicationContext;


    // DataAccessFactoryAdapter， 需要传DataSourceFactory，可以选 SimpleDataSourceFactory 或者 SpringDataSourceFactory
    protected DataAccessFactory dataAccessFactory;

    // DefaultRowMapperFactory
    protected RowMapperFactory rowMapperFactory;

    // SpringInterpreterFactory，会自动带上DefaultInterpreterFactory，还可以配置自己的 interpreter
    protected InterpreterFactory interpreterFactory;


    public ClassPathMapperScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }


    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
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

    public void registerFilters() {
        addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
    }

    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

        if (beanDefinitions.isEmpty()) {
            logger.warn("No Jade mapper was found in '" + this.basePackage
                    + "' package. Please check your configuration.");
        } else {
            for (BeanDefinitionHolder holder : beanDefinitions) {
                GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();

                if (logger.isDebugEnabled()) {
                    logger.debug("Creating JadeDaoFactoryBean with name '" + holder.getBeanName()
                            + "' and '" + definition.getBeanClassName() + "' mapperInterface");
                }
                definition.getPropertyValues().add("objectType", definition.getBeanClassName());

                definition.getPropertyValues().add("dataAccessFactory", dataAccessFactory);
                definition.getPropertyValues().add("rowMapperFactory", rowMapperFactory);
                definition.getPropertyValues().add("interpreterFactory", interpreterFactory);
                definition.setBeanClass(JadeFactoryBean.class);
            }
        }

        return beanDefinitions;
    }

    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            this.logger.warn("Skipping JadeDaoFactoryBean with name \'" + beanName + "\' and \'" + beanDefinition.getBeanClassName() + "\' mapperInterface" + ". Bean already defined with the same name!");
            return false;
        }
    }
}
