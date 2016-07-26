package net.paoding.rose.jade.core;

import net.paoding.rose.jade.provider.DataAccess;
import net.paoding.rose.jade.provider.Modifier;

/**
 * 定义创建: {@link JdbcOperation} 的工厂。
 * 
 * @author han.liao
 */
public interface JdbcOperationFactory {

    /**
     * 创建: {@link JdbcOperation} 对象。
     * 
     * @return {@link JdbcOperation} 对象
     */
    public JdbcOperation getJdbcOperation(DataAccess dataAccess, Modifier modifier);
}
