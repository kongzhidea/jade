package net.paoding.rose.jade.statement;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 王志亮 [qieqie.wang@gmail.com]
 */
public class StatementRuntimeImpl implements StatementRuntime {

    private final StatementMetaData metaData;

    private final Map<String, Object> parameters;

    private String sql;

    private Object[] args;

    private Map<String, Object> attributes;

    public StatementRuntimeImpl(StatementMetaData metaData, Map<String, Object> parameters) {
        this.metaData = metaData;
        this.parameters = parameters;
        this.sql = metaData.getSQL();
    }

    @Override
    public StatementMetaData getMetaData() {
        return metaData;
    }

    @Override
    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    @Override
    public void setSQL(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSQL() {
        return sql;
    }

    @Override
    public Object[] getArgs() {
        return args;
    }

    @Override
    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            return Collections.emptyMap();
        }
        return this.attributes;
    }

    @Override
    public void setAttribute(String name, Object value) {
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }
        this.attributes.put(name, value);
    }

    @Override
    public Object getAttribute(String name) {
        return (attributes == null ? null : attributes.get(name));
    }

    @Override
    public String toString() {
        return "StatementRuntimeImpl{" +
                "metaData=" + metaData +
                ", parameters=" + parameters +
                ", sql='" + sql + '\'' +
                ", args=" + Arrays.toString(args) +
                ", attributes=" + attributes +
                '}';
    }
}
