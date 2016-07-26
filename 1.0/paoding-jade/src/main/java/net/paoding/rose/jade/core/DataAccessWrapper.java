package net.paoding.rose.jade.core;

import java.util.List;
import java.util.Map;

import net.paoding.rose.jade.provider.DataAccess;
import net.paoding.rose.jade.provider.Modifier;

import org.springframework.jdbc.core.RowMapper;

/**
 * 装饰者模式
 *
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */

public class DataAccessWrapper implements DataAccess {

    protected DataAccess targetDataAccess;

    public DataAccessWrapper() {
    }

    public DataAccessWrapper(DataAccess dataAccess) {
        this.targetDataAccess = dataAccess;
    }

    public void setDataAccess(DataAccess dataAccess) {
        this.targetDataAccess = dataAccess;
    }

    @Override
    public List<?> select(String sql, Modifier modifier, Map<String, Object> parameters,
                          RowMapper rowMapper) {
        return targetDataAccess.select(sql, modifier, parameters, rowMapper);
    }

    @Override
    public int update(String sql, Modifier modifier, Map<String, Object> parameters) {
        return targetDataAccess.update(sql, modifier, parameters);
    }

    @Override
    public Number insertReturnId(String sql, Modifier modifier, Map<String, Object> parameters) {
        return targetDataAccess.insertReturnId(sql, modifier, parameters);
    }

    @Override
    public int[] batchUpdate(String sql, Modifier modifier, List<Map<String, Object>> parametersList) {
        return targetDataAccess.batchUpdate(sql, modifier, parametersList);
    }

}
