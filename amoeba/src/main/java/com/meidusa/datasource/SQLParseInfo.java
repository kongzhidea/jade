package com.meidusa.datasource;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meidusa.amoeba.mysql.parser.sql.MysqlParser;
import com.meidusa.amoeba.parser.dbobject.Column;
import com.meidusa.amoeba.parser.dbobject.Table;
import com.meidusa.amoeba.parser.expression.BaseExpressionList;
import com.meidusa.amoeba.parser.expression.ColumnExpression;
import com.meidusa.amoeba.parser.expression.ComparisonExpression;
import com.meidusa.amoeba.parser.expression.ConstantExpression;
import com.meidusa.amoeba.parser.expression.Expression;
import com.meidusa.amoeba.parser.expression.FunctionExpression;
import com.meidusa.amoeba.parser.expression.ParameterExpression;
import com.meidusa.amoeba.parser.function.Function;
import com.meidusa.amoeba.parser.function.UnknowableFunction;
import com.meidusa.amoeba.parser.statment.DMLStatment;
import com.meidusa.amoeba.parser.statment.DeleteStatment;
import com.meidusa.amoeba.parser.statment.InsertStatment;
import com.meidusa.amoeba.parser.statment.SelectStatment;
import com.meidusa.amoeba.parser.statment.Statment;
import com.meidusa.amoeba.parser.statment.UpdateStatment;

public class SQLParseInfo {

    // 输出日志
    protected static final Log logger = LogFactory.getLog(SQLParseInfo.class);

    // 常用函数列表
    protected static Map<String, Function> functionMap = new HashMap<String, Function>();

    static {
        //  [NEED-FIX] 避免在解析这些函数时出错，并不实际解析函数本身。这意味着会忽略函数参数中的问号,
        // 会造成某些语句的参数顺序解析错误。
        functionMap.put("MOD", new UnknowableFunction());
        functionMap.put("CONCAT", new UnknowableFunction());
        functionMap.put("CONCAT_WS", new UnknowableFunction());
        functionMap.put("NOW", new UnknowableFunction());
        functionMap.put("SYSDATE", new UnknowableFunction());
        functionMap.put("CURRENT_TIMESTAMP", new UnknowableFunction());
        functionMap.put("CURRENT_DATE", new UnknowableFunction());
        functionMap.put("ADDDATE", new UnknowableFunction());
        // 如果添加, 会忽略以 'year'  'month' 'day' 'hour' 'minute' 'second' 'microsecond' 为名称
        // 的列查询条件。
        // functionMap.put("YEAR", new UnknowableFunction());
        // functionMap.put("MONTH", new UnknowableFunction());
        // functionMap.put("WEEK", new UnknowableFunction());
        // functionMap.put("DAY", new UnknowableFunction());
        // functionMap.put("HOUR", new UnknowableFunction());
        // functionMap.put("MINUTE", new UnknowableFunction());
        // functionMap.put("SECOND", new UnknowableFunction());
        // functionMap.put("MICROSECOND", new UnknowableFunction());
        functionMap.put("ASCII", new UnknowableFunction());
        functionMap.put("NEXTVAL", new UnknowableFunction());
        functionMap.put("INSERT", new UnknowableFunction());
        functionMap.put("INSTR", new UnknowableFunction());
        functionMap.put("SUBSTRING", new UnknowableFunction());
        functionMap.put("DATABASE", new UnknowableFunction());
        functionMap.put("LAST_INSERT_ID", new UnknowableFunction());
    }

    // 缓存语句的解析信息
    /* sql->SQLParseInfo */
    protected static LRUMap cacheInfo = new LRUMap(1024); // [NEED-CODE] 大小需要配置

    public static SQLParseInfo getParseInfo(String sql) {

        SQLParseInfo info;

        // 进入全局锁创建共享信息对象。
        synchronized (cacheInfo) {

            info = (SQLParseInfo) cacheInfo.get(sql);

            if (info == null) {

                // 创建  XceParseInfo 实例是一个不耗时的操作。
                info = new SQLParseInfo(sql);
                cacheInfo.put(sql, info);
            }
        }

        // 解析所给的语句, 也许比较耗时。
        info.parseInfoIfNecessary();

        return info;
    }

    // 当前的  SQL 语句
    protected final String sql;

    // 当前的查询结果
    protected boolean dml;

    protected boolean select, insert, update, delete;

    // 查询的数据表
    protected Table[] tables;

    // 查询的所有命名常量或问号参数（仅是where中的参数，select中的，update set中的都不算）
    protected HashMap<Column, Object> namedParams = new HashMap<Column, Object>();

    // 问号参数的索引（仅是where中的问号参数，update set中的都不算）
    protected ArrayList<Column> indexParams = new ArrayList<Column>();

    protected int paramOffset = 0, paramCount = 0; // 全部命名参数个数

    //    protected boolean parsingFailed = false;
    protected boolean inited = false;

    private int parameterExpressionCount;

    /**
     * 默认的构造函数。
     */
    public SQLParseInfo(String sql) {
        this.sql = sql;
    }

    /**
     * 获取解析的原始语句。
     *
     * @return 原始语句
     */
    public String getSQL() {
        return sql;
    }

    /**
     * 检查解析信息是否已经初始化。
     *
     * @return 解析信息是否初始化
     */
    public boolean isInited() {
        return inited;
    }

    //    /**
    //     * 检查语句解析是否失败。
    //     * 
    //     * @return 语句解析是否失败
    //     */
    //    public boolean isParsingFailed() {
    //        return parsingFailed;
    //    }

    /**
     * 当必要的时候，从指定的语句解析信息。
     */
    protected void parseInfoIfNecessary() {

        // 下面进行耗时操作，单独锁定记录本身。
        if (inited) {
            return;
        }
        synchronized (this) {
            if (inited) {
                return;
            }
            parseInfo();
            inited = true;
        }
    }

    /**
     * 从指定的语句解析信息。
     */
    private void parseInfo() {

        // 输出日志
        if (logger.isDebugEnabled()) {
            logger.debug("First parsing SQL: " + sql);
        }

        String parseSql = sql;

        if (sql.startsWith("select nextval(")) {
            if (logger.isDebugEnabled()) {
                logger.debug("it's a select nextval sql: " + sql);
            }
            dml = true;
            select = true;
            return;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("it's not a  select nextval sql: " + sql);
            }
        }

        // 使用  Amoeba 自带的解析器分析查询语句。
        MysqlParser parser = new MysqlParser(new StringReader(parseSql));

        // 设置函数列表。
        parser.setFunctionMap(functionMap);

        try {
            Statment stmt = parser.doParse();

            dml = (stmt instanceof DMLStatment);

            if (dml) {
                // 解析  SELECT / INSERT / UPDATE / DELETE 语句                
                select = (stmt instanceof SelectStatment);
                insert = (stmt instanceof InsertStatment);
                update = (stmt instanceof UpdateStatment);
                delete = (stmt instanceof DeleteStatment);

                DMLStatment dmlStmt = (DMLStatment) stmt;

                tables = dmlStmt.getTables();

                // 处理生成的表达式
                parseExpr(dmlStmt.getExpression(), null);

                if (dmlStmt instanceof UpdateStatment) {
                    // 记录参数数目，注意: UPDATE 语句的实际参数数目要大于表达式中提取的参数数目。因此，真实地参数索引应该是: 
                    // select语句的limit ?, ?无法计算到parameterExpressionCount中，比较杯具(但限制了UPDATE语句才有效，所以无所谓)
                    paramOffset = stmt.getParameterCount() - parameterExpressionCount;
                }
                paramCount += stmt.getParameterCount();
            }

        } catch (Throwable e) {

            // 语句解析失败，写出日志。
            logger.error("Parsing SQL failed: " + sql, e);
            //            throw new BadSqlGrammarException("jade-parsing", sql, new SQLException(e));
        }
    }

    /**
     * 检查是否为: INSERT / UPDATE / DELETE / SELECT 语句。
     *
     * @return 是否为数据操作语句
     */
    public boolean isDML() {
        return dml;
    }

    /**
     * 检查是否为 : SELECT 语句。
     *
     * @return 是否为查询语句
     */
    public boolean isSelect() {
        return select;
    }

    /**
     * 检查是否为 : INSERT 语句。
     *
     * @return 是否为查询语句
     */
    public boolean isInsert() {
        return insert;
    }

    /**
     * 检查是否为 : UPDATE 语句。
     *
     * @return 是否为查询语句
     */
    public boolean isUpdate() {
        return update;
    }

    /**
     * 检查是否为 : DELETE 语句。
     *
     * @return 是否为查询语句
     */
    public boolean isDelete() {
        return delete;
    }

    /**
     * 检查是否特殊查询，特殊查询是类似： SELECT @@LAST_INSERT_ID / SELECT LAST_INSERT_ID()
     * / SELECT FOUND_ROWS() 此类的查询，必须与前一个查询连用。
     *
     * @return 是否特殊查询
     */
    public boolean isUnswitch() {

        // 暂时没有办法做更加复杂的检测。
        return (select && (0 == tables.length));
    }

    /**
     * 返回查询的数据表。
     *
     * @return 查询的数据表
     */
    public Table[] getTables() {
        return tables;
    }

    /**
     * 返回参数数目。
     *
     * @return 参数数目
     */
    public int getParamCount() {
        return paramCount;
    }


    /**
     * 返回指定列的索引位置。 prepareStatement中的column顺序, 从0开始
     *
     * @param column - 指定列
     * @return 列的索引位置，从0开始
     */
    public int getColumnIndex(Column column) {

        int index = indexParams.indexOf(column);

        if (index >= 0) {
            return index + paramOffset;
        }

        return -1;
    }

    /**
     * 检查对应列的查询参数是否存在。
     *
     * @param column - 对应的列
     * @return 参数是否存在
     */
    public boolean containsParam(Column column) {

        return namedParams.containsKey(column);
    }

    /**
     * 获得对应列的参数值。
     *
     * @param column - 对应的列
     * @return 参数值
     */
    public Object getParam(Column column) {

        return namedParams.get(column);
    }

    /**
     * 获得内部函数映射表。
     *
     * @return 内部函数映射表
     */
    public static Map<String, Function> getFunctionMap() {
        return functionMap;
    }

    /**
     * 创建一个 {@link Table} 对象。
     */
    public static Table newTable(String tableName) {
        Table table = new Table();
        table.setName(tableName);
        return table;
    }

    /**
     * 创建一个 {@link Table} 对象。
     */
    public static Table newTable(String tableName, String aliasName) {
        Table table = new Table();
        table.setName(tableName);
        table.setAlias(aliasName);
        return table;
    }

    /**
     * 创建一个 {@link Column} 对象。
     */
    public static Column newColumn(String composeName) {
        final int index = composeName.indexOf('.');
        if (index >= 0) {
            return newColumn(composeName.substring(0, index), // NL
                    composeName.substring(index + 1));
        }
        return newColumn((Table) null, composeName);
    }

    /**
     * 创建一个 {@link Column} 对象。
     */
    public static Column newColumn(String tableName, String columnName) {
        Column column = new Column();
        column.setTable(newTable(tableName));
        column.setName(columnName.toUpperCase());
        return column;
    }

    /**
     * 创建一个 {@link Column} 对象。
     */
    public static Column newColumn(Table table, String columnName) {
        Column column = new Column();
        column.setTable(table);
        column.setName(columnName.toUpperCase());
        return column;
    }

    /**
     * 处理生成的表达式。
     *
     * @param expr   - 生成的表达式
     * @param column - 当前的列, 可能为 <code>null</code>.
     */
    protected void parseExpr(Expression expr, Column column) {

        if (expr instanceof BaseExpressionList) {

            BaseExpressionList exprList = (BaseExpressionList) expr;

            List<Expression> list = exprList.getAllExpression();

            for (Expression exp : list) {
                parseExpr(exp, null);
            }

        } else if (expr instanceof ColumnExpression) {

            ColumnExpression columnExpr = (ColumnExpression) expr;

            column = columnExpr.getColumn();

            parseExpr(columnExpr.getExpression(), column);

        } else if (expr instanceof ComparisonExpression) {

            ComparisonExpression compExpr = (ComparisonExpression) expr;

            parseExpr(compExpr.getExpression(), column);

        } else if (expr instanceof ParameterExpression) {
            parameterExpressionCount++;
            indexParams.add(column);
            namedParams.put(column, null);

        } else if (expr instanceof ConstantExpression) {

            // 注：evaluate 返回常量的值。
            // indexParams.add(column);
            namedParams.put(column, expr.evaluate(null));

        } else if (expr instanceof FunctionExpression) {

            FunctionExpression funcExpr = (FunctionExpression) expr;

            List<Expression> list = funcExpr.getArgList();

            for (Expression exp : list) {
                parseExpr(exp, null);
            }
        }
    }

    @Override
    public String toString() {
        return "SQLParseInfo{" +
                "sql='" + sql + '\'' +
                ", dml=" + dml +
                ", select=" + select +
                ", insert=" + insert +
                ", update=" + update +
                ", delete=" + delete +
                ", tables=" + Arrays.toString(tables) +
                ", namedParams=" + namedParams +
                ", indexParams=" + indexParams +
                ", paramOffset=" + paramOffset +
                ", paramCount=" + paramCount +
                ", inited=" + inited +
                ", parameterExpressionCount=" + parameterExpressionCount +
                '}';
    }
}
