package net.paoding.rose.jade.dataaccess;

import java.util.Map;

import net.paoding.rose.jade.statement.StatementMetaData;
import net.paoding.rose.jade.statement.StatementRuntime;

/**
 * 这是框架的内部接口，{@link DataAccess}的工厂类。
 *
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
public interface DataAccessFactory {

    /**
     * 运行时为框架提供一个 {@link DataAccess} 实例
     * @param metaData 所执行的DAO方法
     * @param attributes {@link StatementRuntime#getAttributes()}
     */
    DataAccess getDataAccess(StatementMetaData metaData, Map<String, Object> attributes);
}
