package net.paoding.rose.jade.provider;

import javax.sql.DataSource;

import net.paoding.rose.jade.core.DataAccessWrapper;
import net.paoding.rose.jade.core.SQLThreadLocalWrapper;
import net.paoding.rose.jade.datasource.DataSourceFactory;

/**
 * 基本的 {@link DataAccessProvider} 实现, 子类可以实现以下两个抽象方法提供定制的
 * {@link DataAccess} 与 {@link DataSourceFactory} 实现。
 * <p/>
 * <ul>
 * <li>
 * {@link AbstractDataAccessProvider#createDataAccess(javax.sql.DataSource)}
 * <li> {@link AbstractDataAccessProvider#createDataSourceFactory()}
 * </ul>
 *
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
public abstract class AbstractDataAccessProvider implements DataAccessProvider {

    // 是否使用装饰者模式，默认使用
    private boolean wrapper = true;

    protected DataSourceFactory dataSourceFactory;

    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    public void setWrapper(boolean wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public DataAccess createDataAccess(Class<?> daoClass) {

        if (dataSourceFactory == null) {
            dataSourceFactory = createDataSourceFactory();
        }

        DataSource dataSource = dataSourceFactory.getDataSource(daoClass);
        if (dataSource == null) {
            throw new NullPointerException("not found dataSource for dao: '" + daoClass.getName()
                    + "'.");
        }

        DataAccess dataAccess = createDataAccess(dataSource);
        if (!wrapper) {
            return dataAccess;
        }
        if (dataAccess instanceof DataAccessWrapper) {
            return dataAccess;
        }
        SQLThreadLocalWrapper dataAccessProvider = new SQLThreadLocalWrapper();
        dataAccessProvider.setDataAccess(dataAccess);
        return dataAccessProvider;
    }

    /**
     * 重载方法创建自己的 {@link DataAccess} 实现。
     *
     * @param dataSource - 数据源
     * @return {@link DataAccess} 实现
     */
    protected abstract DataAccess createDataAccess(DataSource dataSource);

    /**
     * 重载方法创建自己的 {@link DataSourceFactory} 实现。
     *
     * @return {@link DataSourceFactory} 实现
     */
    protected abstract DataSourceFactory createDataSourceFactory();
}
