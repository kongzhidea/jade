package net.paoding.rose.jade.route.instance;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.BadSqlGrammarException;

/**
 * 实现: HASH 算法进行散表的配置记录。
 */
public class HashRouter implements Router {

    // 输出日志
    protected static final Log logger = LogFactory.getLog(HashRouter.class);

    protected String column, pattern;

    protected int count;

    /**
     * 创建配置记录。
     *
     * @param column  - 配置的列
     * @param pattern - 数据表的名称模板
     * @param count   - 散列表数目
     */
    public HashRouter(String column, String pattern, int count) {
        this.column = column;
        this.pattern = pattern;
        this.count = count;
    }

    @Override
    public String getColumn() {
        return column;
    }

    /**
     * 设置配置的列。
     *
     * @param column - 配置的列
     */
    public void setColumn(String column) {
        this.column = column;
    }

    /**
     * 返回数据表的名称模板。
     *
     * @return 数据表的名称模板
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * 设置数据表的名称模板。
     *
     * @param pattern - 数据表的名称模板
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * 返回散列表数目。
     *
     * @return 散列表数目
     */
    public int getCount() {
        return count;
    }

    /**
     * 设置散列表数目。
     *
     * @param count - 散列表数目
     */
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String doRoute(Object columnValue) {

        if (pattern != null && columnValue != null) {

            long longValue = convert(columnValue);
            if (longValue < 0) {
                longValue = -longValue;
            }

            int value = (int) (longValue % count);

            String name = MessageFormat.format(pattern, value);

            // 输出日志
            if (logger.isDebugEnabled()) {
                logger.debug("Routing on [" + column + " = " + columnValue + ", "
                        + columnValue.getClass() + "]: " + name);
            }

            return name;
        }

        return null;
    }

    protected long convert(Object columnValue) {

        if (columnValue instanceof Number) {

            return ((Number) columnValue).longValue();

        } else {

            try {
                // 转换成字符串处理
                return Long.parseLong(String.valueOf(columnValue));

            } catch (NumberFormatException e) {

                // 输出日志
                if (logger.isWarnEnabled()) {
                    logger.warn("Column \'" + column // NL
                            + "\' must be number, but: " + columnValue);
                }

                throw new BadSqlGrammarException("HashRouter.convert", "Column \'" + column // NL
                        + "\' must be number, but: " + columnValue, null);
            }
        }
    }
}
