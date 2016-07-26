package net.paoding.rose.jade.core;

import java.lang.reflect.Proxy;

import net.paoding.rose.jade.provider.DataAccess;
import net.paoding.rose.jade.provider.DataAccessProvider;
import net.paoding.rose.jade.provider.Definition;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * 提供 DAO 对象的 Spring-framework {@link FactoryBean} 工厂。
 *
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
public class JadeDaoFactoryBean<T> implements FactoryBean, InitializingBean, BeanNameAware {
    private String beanName;

    private T dao;

    private Class<T> daoClass;

    // 保存dataAccessProvider而非dataAccess是为了尽量延迟获取DataAccess实例
    private DataAccessProvider dataAccessProvider;

    public void setDaoClass(Class<T> daoClass) {
        this.daoClass = daoClass;
    }

    public void setDaoClass(String daoClass) {
        try {
            this.daoClass = (Class<T>) Class.forName(daoClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setDataAccessProvider(DataAccessProvider dataAccessProvider) {
        this.dataAccessProvider = dataAccessProvider;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(dataAccessProvider);
        Assert.isTrue(daoClass.isInterface(), "not a interface class: " + daoClass.getName());
    }

    @Override
    public T getObject() {
        if (dao == null) {
            synchronized (this) {
                if (dao == null) {
                    dao = createDAO(daoClass);
                }
            }
        }
        Assert.notNull(dao);
        return dao;
    }

    @SuppressWarnings("unchecked")
    protected T createDAO(Class<T> daoClass) {
        Definition definition = new Definition(daoClass);
        DataAccess dataAccess = dataAccessProvider.createDataAccess(daoClass);
        JadeDaoInvocationHandler handler = new JadeDaoInvocationHandler(dataAccess, definition);
        return (T) Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(),
                new Class[]{daoClass}, handler);
    }

    @Override
    public Class<T> getObjectType() {
        return daoClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setBeanName(String s) {
        this.beanName = s;
    }
}
