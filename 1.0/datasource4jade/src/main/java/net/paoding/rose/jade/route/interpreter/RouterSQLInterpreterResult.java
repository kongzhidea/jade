package net.paoding.rose.jade.route.interpreter;

import net.paoding.rose.jade.datasource.XnDataSource;
import net.paoding.rose.jade.provider.SQLInterpreterResult;

public class RouterSQLInterpreterResult implements SQLInterpreterResult {

    private String dbPattern = XnDataSource.EMPTY_PATTERN;

    private String sql;

    private Object[] parameters;

    public RouterSQLInterpreterResult(String dbPattern, String sql, Object[] parameters) {
        if (dbPattern != null) {
            this.dbPattern = dbPattern;
        }
        this.sql = sql;
        this.parameters = parameters;
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public String getSQL() {
        return sql;
    }

    public String getDbPattern() {
        return this.dbPattern;
    }

}
