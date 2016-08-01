package net.paoding.rose.jade.datasource;

import net.paoding.rose.jade.annotation.DAO;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 替换 SpringDataSourceFactory，可以实现主从分离
 */
public class XnDataSourceFactory implements DataSourceFactory, ApplicationContextAware {

    private SpringDataSourceFactory inner = new SpringDataSourceFactory();

    private Map<String, DataSource> cached = new HashMap<String, DataSource>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.inner.setApplicationContext(applicationContext);
    }

    @Override
    public DataSource getDataSource(Class<?> daoClass) {
        DataSource dataSource = null;
        try {
            dataSource = inner.getDataSource(daoClass);
        } catch (IllegalArgumentException e) {
        }
        if (dataSource == null) {
            String catalog = daoClass.getAnnotation(DAO.class).catalog();
            if (catalog != null && catalog.length() > 0) {
                dataSource = cached.get(catalog);
                if (dataSource == null) {
                    dataSource = new XnDataSource(catalog);
                    cached.put(catalog, dataSource);
                }
            }
        }
        return dataSource;
    }
}
