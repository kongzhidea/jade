package net.paoding.rose.jade.context.spring;

import net.paoding.rose.jade.context.JadeInvocationHandler;
import net.paoding.rose.jade.dataaccess.DataAccessFactory;
import net.paoding.rose.jade.rowmapper.RowMapperFactory;
import net.paoding.rose.jade.statement.DAOConfig;
import net.paoding.rose.jade.statement.DAOMetaData;
import net.paoding.rose.jade.statement.InterpreterFactory;
import net.paoding.rose.jade.statement.StatementWrapperProvider;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Proxy;

/**
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
@SuppressWarnings("rawtypes")
public class JadeFactoryBean implements FactoryBean, InitializingBean {

    protected Class<?> objectType;

    // DataAccessFactoryAdapter， 需要传DataSourceFactory，可以选 SimpleDataSourceFactory 或者 SpringDataSourceFactory
    protected DataAccessFactory dataAccessFactory;

    // DefaultRowMapperFactory
    protected RowMapperFactory rowMapperFactory;

    // SpringInterpreterFactory，会自动带上DefaultInterpreterFactory，还可以配置自己的 interpreter
    protected InterpreterFactory interpreterFactory;

    protected Object daoObject;

    // 可选的
    private StatementWrapperProvider statementWrapperProvider;

    public JadeFactoryBean() {
    }

    @Override
    public Class<?> getObjectType() {
        return objectType;
    }

    public void setObjectType(Class<?> objectType) {
        this.objectType = objectType;
    }

    public void setObjectType(String objectType) {
        try {
            this.objectType = Class.forName(objectType);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param dataAccessFactory
     */
    public void setDataAccessFactory(DataAccessFactory dataAccessFactory) {
        this.dataAccessFactory = dataAccessFactory;
    }

    public DataAccessFactory getDataAccessFactory() {
        return dataAccessFactory;
    }

    /**
     * @param rowMapperFactory
     */
    public void setRowMapperFactory(RowMapperFactory rowMapperFactory) {
        this.rowMapperFactory = rowMapperFactory;
    }

    public RowMapperFactory getRowMapperFactory() {
        return rowMapperFactory;
    }

    /**
     * @param interpreterFactory
     */
    public void setInterpreterFactory(InterpreterFactory interpreterFactory) {
        this.interpreterFactory = interpreterFactory;
    }

    public InterpreterFactory getInterpreterFactory() {
        return interpreterFactory;
    }

    public StatementWrapperProvider getStatementWrapperProvider() {
        return statementWrapperProvider;
    }

    public void setStatementWrapperProvider(StatementWrapperProvider statementWrapperProvider) {
        this.statementWrapperProvider = statementWrapperProvider;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.isTrue(objectType.isInterface(), "not a interface class: " + objectType.getName());
        Assert.notNull(dataAccessFactory);
        Assert.notNull(rowMapperFactory);
        Assert.notNull(interpreterFactory);
        // cacheProvider可以null，不做assert.notNull判断
    }

    @Override
    public Object getObject() {
        if (daoObject == null) {
            daoObject = createDAO();
            Assert.notNull(daoObject);
        }
        return daoObject;
    }

    protected Object createDAO() {
        try {
            DAOConfig config = new DAOConfig(dataAccessFactory, rowMapperFactory,
                    interpreterFactory, statementWrapperProvider);
            DAOMetaData daoMetaData = new DAOMetaData(objectType, config);
            JadeInvocationHandler handler = new JadeInvocationHandler(daoMetaData);
            return Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(),
                    new Class[]{objectType}, handler);
        } catch (RuntimeException e) {
            throw new IllegalStateException(
                    "failed to create bean for " + this.objectType.getName(), e);
        }
    }

}
