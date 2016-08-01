package net.paoding.rose.jade.datasource;

import net.paoding.rose.jade.datasource.provider.SimpleDataSourceProvider;
import net.paoding.rose.jade.statement.StatementMetaData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * 可以实现主从分离，不支持分表
 */
public class XnDataSource extends AbstractDataSource {
    public static final String DB_PATTERN = XnDataSource.class.getName() + "#!!!";
    public static final String EMPTY_PATTERN = "";

    // TODO 实际生产环境 需要自己实现，从服务端来获取
    DataSourceProvider dataSourceProvider = new SimpleDataSourceProvider();

    protected final Log logger = LogFactory.getLog(XnDataSource.class);

    private String bizName;

    boolean master;// true 主库， false 从库

    StatementMetaData metaData;

    Map<String, Object> runtimeProperties;

    public XnDataSource() {
    }

    public XnDataSource(String catalog, boolean master, StatementMetaData metaData, Map<String, Object> runtimeProperties) {
        setBizName(catalog);
        this.master = master;
        this.metaData = metaData;
        this.runtimeProperties = runtimeProperties;
    }


    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public String getBizName() {
        return bizName;
    }

    public Connection getConnection() throws SQLException {
        String pattern = (String) runtimeProperties.get(XnDataSource.DB_PATTERN);
        if (pattern == null) {
            pattern = EMPTY_PATTERN;
        }
        Connection conn;
        if (master) {
            conn = dataSourceProvider.getWriteDataSource(bizName, pattern).getConnection();
        } else {
            conn = dataSourceProvider.getReadDataSource(bizName, pattern).getConnection();
        }
        if (conn == null) {
            throw new SQLException("could't get " + (master ? "Write" : "Read")
                    + " connection from bizName '" + bizName + "' for pattern '" + pattern + "'");
        }
        return conn;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }
}
