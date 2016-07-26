package net.paoding.rose.jade.dataaccess;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import net.paoding.rose.jade.statement.StatementMetaData;

/**
 * 框架内部使用的 {@link DataAccessFactory}实现，适配到 {@link DataSourceFactory}
 * ，由后者提供最终的数据源
 *
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @see DataSourceFactory
 */
public class DataAccessFactoryAdapter implements DataAccessFactory {

    private DataSourceFactory dataSourceFactory;
    private ConcurrentHashMap<DataSource, DataAccess> dataAccessCache;

    public DataAccessFactoryAdapter() {
        this.dataAccessCache = new ConcurrentHashMap<DataSource, DataAccess>();
    }

    public DataAccessFactoryAdapter(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
        this.dataAccessCache = new ConcurrentHashMap<DataSource, DataAccess>();
    }

    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    @Override
    public DataAccess getDataAccess(StatementMetaData metaData, Map<String, Object> attributes) {
        DataSourceHolder holder = dataSourceFactory.getHolder(metaData, attributes);
        while (holder != null && holder.isFactory()) {
            holder = holder.getFactory().getHolder(metaData, attributes);
        }
        if (holder == null || holder.getDataSource() == null) {
            throw new NullPointerException("cannot found a dataSource for: " + metaData);
        }
        DataSource dataSource = holder.getDataSource();
        DataAccess dataAccess = dataAccessCache.get(dataSource);
        if (dataAccess == null) {
            dataAccessCache.putIfAbsent(dataSource, new DataAccessImpl(dataSource));
            dataAccess = dataAccessCache.get(dataSource);
        }
        return dataAccess;
    }
}
