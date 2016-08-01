package net.paoding.rose.jade.route.interpreter;

import com.meidusa.amoeba.parser.dbobject.Column;
import com.meidusa.amoeba.parser.dbobject.Table;
import com.meidusa.datasource.SQLParseInfo;
import com.meidusa.datasource.SqlRewriter;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.datasource.XnDataSource;
import net.paoding.rose.jade.route.RoutingDescriptor;
import net.paoding.rose.jade.route.RoutingInfo;
import net.paoding.rose.jade.route.parse.RemoteXmlDocConfigurator;
import net.paoding.rose.jade.route.parse.RoutingConfigurator;
import net.paoding.rose.jade.route.parse.XmlDocConfigurator;
import net.paoding.rose.jade.statement.Interpreter;
import net.paoding.rose.jade.statement.StatementMetaData;
import net.paoding.rose.jade.statement.StatementRuntime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.BadSqlGrammarException;


/**
 * 支持分表 ， 仅支持单表， 不支持sql关联查询，
 * <p/>
 * 配置在系统sql解析器后面
 * <p/>
 * 在spring中配置， SpringInterpreterFactory中会找到此解析器
 */
@Order(9000)
public class RouterInterpreter implements Interpreter {

    private static final Log logger = LogFactory.getLog(RouterInterpreter.class);

    //    RoutingConfigurator routingConfigurator = new XmlDocConfigurator(); // 从classpath中获取
    RoutingConfigurator routingConfigurator = new RemoteXmlDocConfigurator(); // 从本地或者远程服务器活期

    @Override
    public void interpret(StatementRuntime runtime) {

        StatementMetaData metaData = runtime.getMetaData();
        String catalog = metaData.getDAOMetaData().getDAOClass().getAnnotation(DAO.class).catalog();

        String sql = runtime.getSQL();

        SQLParseInfo parseInfo = SQLParseInfo.getParseInfo(sql);
        // 从查询的数据表获取路由配置。
        Table[] tables = parseInfo.getTables();

        RoutingInfo routingInfo = null;
        if (tables != null && tables.length == 1) {
            int beginIndex = 0;
            if (parseInfo.isInsert() && tables.length > 1) {
                // INSERT ... SELECT 查询
                beginIndex = 1;
            }

            // 查找散表配置
            for (int i = beginIndex; i < tables.length; i++) {
                RoutingDescriptor descriptor = routingConfigurator.getDescriptor(catalog, tables[i].getName());
                if (descriptor != null) {
                    routingInfo = new RoutingInfo(tables[i], descriptor);
                    break;
                }
            }
        }
        if (routingInfo == null) {
            return;
        }


        String forwardTableName = null;
        String forwardDbPattern = null;

        // 散表，直接替换sql语句
        if (routingInfo.getTableRouter() != null) {
            // 用语句信息的常量进行散表。
            Column column = routingInfo.getTableRouterColumn();
            Object columnValue = null;

            if (column != null) {
                columnValue = runtime.getParameters().get(":" + (runtime.getMetaData().getShardByIndex() + 1));
                if (columnValue == null) {
                    throw new BadSqlGrammarException("sharding", parseInfo.getSQL(), null);
                }
            }

            // 获得散表的名称
            forwardTableName = routingInfo.getTableRouter().doRoute(columnValue);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("table router is null for sql \"" + sql + "\"");
            }
        }

        //  散库， 设置param， 在XnDataSource中获取散库信息
        if (routingInfo.getDbRouter() != null) {

            // 用语句信息的常量进行散库。
            Column column = routingInfo.getDbRouterColumn();
            Object columnValue = null;

            if (column != null) {
                columnValue = runtime.getParameters().get(":" + (runtime.getMetaData().getShardByIndex() + 1));
                if (columnValue == null) {
                    throw new BadSqlGrammarException("sharding", parseInfo.getSQL(), null);
                }
            }

            // 获得散库的名称
            forwardDbPattern = routingInfo.getDbRouter().doRoute(columnValue);
            if (forwardDbPattern != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("db pattern is '" + forwardDbPattern + "'");
                }
                runtime.setAttribute(XnDataSource.DB_PATTERN, forwardDbPattern);
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("db pattern is empty");
                }
                runtime.setAttribute(XnDataSource.DB_PATTERN, XnDataSource.EMPTY_PATTERN);
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("db router is null for sql \"" + sql + "\"");
            }
        }
        //
        String byTableName = routingInfo.getByTable().getName();
        String sqlRewrited = sql;
        if ((forwardTableName != null) && !forwardTableName.equals(byTableName)) {
            // 使用  SqlRewriter 拆分语句，进行所需的查找和替换。
            sqlRewrited = SqlRewriter.rewriteSqlTable(sql, byTableName, forwardTableName);

            // 输出重写日志
            if (logger.isDebugEnabled()) {
                logger.debug("Rewriting SQL: \n  From: " + sql + "\n  To:   " + sqlRewrited);
            }
        }
        runtime.setSQL(sqlRewrited);
    }
}
