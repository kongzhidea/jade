package net.paoding.rose.jade.datasource;

import javax.sql.DataSource;

import net.paoding.rose.jade.annotation.DAO;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 获取用 Spring Framework 配置的 {@link javax.sql.DataSource} 数据源。
 *
 * @author zhiliang.wang
 */
public class SpringDataSourceFactory implements DataSourceFactory, ApplicationContextAware {

    private Log logger = LogFactory.getLog(getClass());

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public DataSource getDataSource(Class<?> daoClass) {
        String catalog = daoClass.getAnnotation(DAO.class).catalog();
        if (StringUtils.isBlank(catalog)) {
            catalog = daoClass.getName();
        }
        DataSource dataSource = getDataSourceByClassName(catalog);
        logger.info("loading DataSource:" + dataSource);
        return dataSource;
    }

    private DataSource getDataSourceByClassName(String catalog) {
        String tempCatalog = catalog;
        DataSource dataSource;
        while (tempCatalog != null) {
            dataSource = getDataSource(tempCatalog);
            if (dataSource != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("found dataSource 'jade.dataSource." + tempCatalog
                            + "' for catalog '" + catalog + "'.");
                }
                return dataSource;
            }
            int index = tempCatalog.lastIndexOf('.');
            if (index == -1) {
                tempCatalog = null;
            } else {
                tempCatalog = tempCatalog.substring(0, index);
            }
        }
        dataSource = getDataSource("");
        if (dataSource != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("found dataSource 'jade.dataSource' for catalog '" + catalog + "'.");
            }
            return dataSource;
        }
        String key = "dataSource";
        if (applicationContext.containsBean(key)) {
            if (logger.isDebugEnabled()) {
                logger.debug("found dataSource '" + key + "' for catalog '" + catalog + "'.");
            }
            return (DataSource) applicationContext.getBean(key, DataSource.class);
        }
        throw new IllegalArgumentException("not found dataSource for catalog: '" + catalog
                + "'; you should set a dataSource bean"
                + " (with id='jade.dataSource[.daopackage[.daosimpleclassname]]' or 'dataSource' )"
                + "in applicationContext for this catalog.");
    }

    public DataSource getDataSource(String nameSuffix) {
        String key = "jade.dataSource." + nameSuffix;
        if (nameSuffix == null || nameSuffix.length() == 0) {
            key = "jade.dataSource";
        }
        if (applicationContext.containsBean(key)) {
            return (DataSource) applicationContext.getBean(key, DataSource.class);
        }
        return null;
    }
}
