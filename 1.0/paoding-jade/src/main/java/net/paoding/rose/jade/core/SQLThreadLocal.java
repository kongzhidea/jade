package net.paoding.rose.jade.core;

import java.util.Map;

import net.paoding.rose.jade.annotation.SQLType;
import net.paoding.rose.jade.provider.Modifier;

/**
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
public class SQLThreadLocal {

    private static final ThreadLocal<SQLThreadLocal> locals = new ThreadLocal<SQLThreadLocal>();

    public static SQLThreadLocal get() {
        return locals.get();
    }

    public static SQLThreadLocal set(SQLType sqlType, String sql, Modifier modifier,
                                     Map<String, ?> parameters) {
        SQLThreadLocal local = new SQLThreadLocal(sqlType, sql, modifier, parameters);
        locals.set(local);
        return local;
    }

    public static void remove() {
        locals.remove();
    }

    private SQLType sqlType;

    private String sql;

    private Modifier modifier;

    private Map<String, ?> parameters;

    private SQLThreadLocal(SQLType sqlType, String sql, Modifier modifier, Map<String, ?> parameters) {
        this.sqlType = sqlType;
        this.sql = sql;
        this.modifier = modifier;
        this.parameters = parameters;
    }

    public SQLType getSqlType() {
        return sqlType;
    }

    public boolean isReadType() {
        return this.sqlType == SQLType.READ;
    }

    public boolean isWriteType() {
        return this.sqlType == SQLType.WRITE;
    }

    public String getSql() {
        return sql;
    }

    public Modifier getModifier() {
        return modifier;
    }

    public Map<String, ?> getParameters() {
        return parameters;
    }

}
