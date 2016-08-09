package net.paoding.rose.jade.datasource;

import net.paoding.rose.jade.annotation.UseMaster;
import net.paoding.rose.jade.core.SQLThreadLocal;
import net.paoding.rose.jade.datasource.provider.SimpleDataSourceProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * 可以实现主从分离，支持分表
 */
public class XnDataSource extends AbstractDataSource {
    public static final String DB_PATTERN = XnDataSource.class.getName() + "#!!!";
    public static final String EMPTY_PATTERN = "";

    // TODO 实际生产环境 需要自己实现，从服务端来获取
    DataSourceProvider dataSourceProvider = new SimpleDataSourceProvider();

    protected final Log logger = LogFactory.getLog(XnDataSource.class);

    private String bizName;

    public XnDataSource() {
    }

    public XnDataSource(String catalog) {
        setBizName(catalog);
    }


    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public String getBizName() {
        return bizName;
    }

    public Connection getConnection() throws SQLException {
        SQLThreadLocal local = SQLThreadLocal.get();
        Assert.notNull(local, "this is jade's bug; class SQLThreadLocalWrapper "
                + "should override all the DataAccess interface methods.");
        boolean write = false;
        if (local.isWriteType()) {
            write = true;
        } else if (local.getModifier().getMethod().isAnnotationPresent(UseMaster.class)) {
            write = true;
        }
        String pattern = (String) local.getParameters().get(DB_PATTERN);
        if (pattern == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("not found DB_PATTERN, using default patter '' for SQL '"
                        + local.getSql() + "'");
            }
            pattern = EMPTY_PATTERN;
        }
        Connection conn;
        if (write) {
            conn = dataSourceProvider.getWriteDataSource(bizName, pattern).getConnection();
        } else {
            conn = dataSourceProvider.getReadDataSource(bizName, pattern).getConnection();
        }
        if (conn == null) {
            throw new SQLException("could't get " + (write ? "Write" : "Read")
                    + " connection from bizName '" + bizName + "' for pattern '" + pattern + "'");
        }
        return conn;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }
}
