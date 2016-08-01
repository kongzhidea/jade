package net.paoding.rose.jade.datasource;

import javax.sql.DataSource;

/**
 * 第三方获取 datasource，
 * <p/>
 * 可以自己配置从zk或者数据库中获取配置，然后设置DataSource
 */
public interface DataSourceProvider {

    // 从，分表
    public DataSource getReadDataSource(String catalog, String pattern);


    // 主，分表
    public DataSource getWriteDataSource(String catalog, String pattern);
}
