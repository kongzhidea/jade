package net.paoding.rose.jade.datasource;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractDataSource implements DataSource {
    protected final Log logger = LogFactory.getLog(this.getClass());

    public AbstractDataSource() {
    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public void setLoginTimeout(int timeout) throws SQLException {
        throw new UnsupportedOperationException("setLoginTimeout");
    }

    public PrintWriter getLogWriter() {
        throw new UnsupportedOperationException("getLogWriter");
    }

    public void setLogWriter(PrintWriter pw) throws SQLException {
        throw new UnsupportedOperationException("setLogWriter");
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        if(iface.isInstance(this)) {
            return (T) this;
        } else {
            throw new SQLException("DataSource of type [" + this.getClass().getName() + "] cannot be unwrapped as [" + iface.getName() + "]");
        }
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    public Logger getParentLogger() {
        return Logger.getLogger("global");
    }
}
