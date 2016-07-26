package net.paoding.rose.jade.statement;

import net.paoding.rose.jade.dataaccess.DataAccessFactory;
import net.paoding.rose.jade.rowmapper.RowMapperFactory;

/**
 * 支持DAO类的基础配置（数据源配置、SQL解析器配置、OR映射配置等等）
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * 
 */
public class DAOConfig {

    private final DataAccessFactory dataAccessFactory;

    private final RowMapperFactory rowMapperFactory;

    private final InterpreterFactory interpreterFactory;

    private final StatementWrapperProvider statementWrapperProvider;

    public DAOConfig(DataAccessFactory dataAccessFactory, //
                     RowMapperFactory rowMapperFactory, //
                     InterpreterFactory interpreterFactory,
                     StatementWrapperProvider statementWrapperProvider) {
        this.dataAccessFactory = dataAccessFactory;
        this.rowMapperFactory = rowMapperFactory;
        this.interpreterFactory = interpreterFactory;
        this.statementWrapperProvider = statementWrapperProvider;
    }

    /**
     * 标准数据访问器配置
     * 
     * @return
     */
    public DataAccessFactory getDataAccessFactory() {
        return dataAccessFactory;
    }

    /**
     * SQL解析器配置
     * 
     * @return
     */
    public InterpreterFactory getInterpreterFactory() {
        return interpreterFactory;
    }

    /**
     * OR映射配置
     * 
     * @return
     */
    public RowMapperFactory getRowMapperFactory() {
        return rowMapperFactory;
    }

    /**
     * 
     * @return
     */
    public StatementWrapperProvider getStatementWrapperProvider() {
        return statementWrapperProvider;
    }

}
