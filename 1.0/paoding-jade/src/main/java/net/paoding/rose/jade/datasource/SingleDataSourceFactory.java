package net.paoding.rose.jade.datasource;

import java.util.Map;

import javax.sql.DataSource;


/**
 * @author qieqie
 */
public class SingleDataSourceFactory implements DataSourceFactory {

    private DataSource dataSource;

    public SingleDataSourceFactory() {
    }

    public SingleDataSourceFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public DataSource getDataSource(Class<?> daoClass) {
        return dataSource;
    }
}
