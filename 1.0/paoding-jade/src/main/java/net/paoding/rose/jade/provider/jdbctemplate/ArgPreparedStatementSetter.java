package net.paoding.rose.jade.provider.jdbctemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;

/**
 * 类代码从 {@link org.springframework.jdbc.core.ArgPreparedStatementSetter}
 * 复制，原始类是非 public 的。
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
public class ArgPreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer {

    private final Object[] args;

    /**
     * Create a new ArgPreparedStatementSetter for the given arguments.
     * 
     * @param args the arguments to set
     */
    public ArgPreparedStatementSetter(Object[] args) {
        this.args = args;
    }

    public void setValues(PreparedStatement ps) throws SQLException {
        if (this.args != null) {
            for (int i = 0; i < this.args.length; i++) {
                Object arg = this.args[i];
                if (arg instanceof SqlParameterValue) {
                    SqlParameterValue paramValue = (SqlParameterValue) arg;
                    StatementCreatorUtils.setParameterValue(ps, i + 1, paramValue, paramValue
                            .getValue());
                } else {
                    StatementCreatorUtils.setParameterValue(ps, i + 1, SqlTypeValue.TYPE_UNKNOWN,
                            arg);
                }
            }
        }
    }

    public void cleanupParameters() {
        StatementCreatorUtils.cleanupParameters(this.args);
    }
}
