package net.paoding.rose.jade.core;

import java.util.List;
import java.util.Map;

import net.paoding.rose.jade.annotation.SQLType;
import net.paoding.rose.jade.provider.DataAccess;
import net.paoding.rose.jade.provider.Modifier;

import org.springframework.jdbc.core.RowMapper;

/**
 * 装饰者模式
 * <p/>
 * 使用LocalThread，设置当前线程执行sql为read或者write
 *
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */

public class SQLThreadLocalWrapper extends DataAccessWrapper {

    public SQLThreadLocalWrapper() {
    }

    public SQLThreadLocalWrapper(DataAccess dataAccess) {
        this.targetDataAccess = dataAccess;
    }

    @Override
    public List<?> select(String sql, Modifier modifier, Map<String, Object> parameters,
                          RowMapper rowMapper) {
        SQLThreadLocal.set(SQLType.READ, sql, modifier, parameters);
        try {
            return targetDataAccess.select(sql, modifier, parameters, rowMapper);
        } finally {
            SQLThreadLocal.remove();
        }
    }

    @Override
    public int update(String sql, Modifier modifier, Map<String, Object> parameters) {
        SQLThreadLocal.set(SQLType.WRITE, sql, modifier, parameters);
        try {
            return targetDataAccess.update(sql, modifier, parameters);
        } finally {
            SQLThreadLocal.remove();
        }
    }

    @Override
    public Number insertReturnId(String sql, Modifier modifier, Map<String, Object> parameters) {
        SQLThreadLocal.set(SQLType.WRITE, sql, modifier, parameters);
        try {
            return targetDataAccess.insertReturnId(sql, modifier, parameters);
        } finally {
            SQLThreadLocal.remove();
        }
    }

}
