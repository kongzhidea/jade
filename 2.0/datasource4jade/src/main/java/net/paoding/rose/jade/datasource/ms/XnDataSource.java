package net.paoding.rose.jade.datasource.ms;

import net.paoding.rose.jade.datasource.AbstractDataSource;
import net.paoding.rose.jade.datasource.DataSourceProvider;
import net.paoding.rose.jade.datasource.provider.SimpleDataSourceProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 可以实现主从分离，不支持分表
 */
public class XnDataSource extends AbstractDataSource {

    // TODO 实际生产环境 需要自己实现，从服务端来获取
    DataSourceProvider dataSourceProvider = new SimpleDataSourceProvider();

    protected final Log logger = LogFactory.getLog(XnDataSource.class);

    private String bizName;

    private XnDataSourceFactory dataSourceFactory;

    boolean master;// true 主库， false 从库


    public XnDataSource() {
    }

    public XnDataSource(XnDataSourceFactory dataSourceFactory, String catalog, boolean master) {
        this.dataSourceFactory = dataSourceFactory;
        setBizName(catalog);
        this.master = master;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public String getBizName() {
        return bizName;
    }

    public Connection getConnection() throws SQLException {
        String pattern = DataSourceProvider.EMPTY_PATTERN;
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
