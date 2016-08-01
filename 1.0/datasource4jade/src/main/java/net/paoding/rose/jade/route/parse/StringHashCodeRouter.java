package net.paoding.rose.jade.route.parse;

import net.paoding.rose.jade.route.instance.HashRouter;

/**
 * 实现: String.hashCode() mod 算法进行散表的配置记录。
 * <p/>
 * 只取字符串的后两位字符进行散表。
 *
 * @author han.liao
 */
public class StringHashCodeRouter extends HashRouter {

    /**
     * 创建配置记录。
     *
     * @param column  - 配置的列
     * @param pattern - 数据表的名称模板
     * @param count   - 散列表数目
     */
    public StringHashCodeRouter(String column, String pattern, int count) {
        super(column, pattern, count);
    }

    @Override
    protected long convert(Object columnValue) {

        if (columnValue == null) {
            throw new NullPointerException("[StringHashCodeRouter] Column \'" + column // NL
                    + "\'");
        }

        return String.valueOf(columnValue).hashCode();

    }

}
