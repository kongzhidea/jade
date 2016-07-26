package net.paoding.rose.jade.datasource;

import javax.sql.DataSource;

/**
 * 定义创建 {@link javax.sql.DataSource} 的工厂。
 *
 * @author han.liao
 */
public interface DataSourceFactory {

    /**
     * @param daoClass - 数据源名称
     * @return {@link javax.sql.DataSource} 实例
     */
    DataSource getDataSource(Class<?> daoClass);
}
