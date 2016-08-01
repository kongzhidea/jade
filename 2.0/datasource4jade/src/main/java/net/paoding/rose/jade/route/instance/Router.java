package net.paoding.rose.jade.route.instance;

import java.sql.SQLException;

/**
 * 提供单个数据表的散表配置。
 * 
 */
public interface Router {

    /**
     * 获取散列的列名。
     * 
     * @return 散列的列名
     */
    String getColumn();

    /**
     * 根据列名的值，计算重定向的散列名。 如果不需要重定向, 可以返回 <code>null</code>.
     * 
     * @param columnValue - 列名的值
     * 
     * @return 重定向的散列名
     * 
     * @throws SQLException
     */
    String doRoute(Object columnValue);
}
