package net.paoding.rose.jade.provider.jdbctemplate;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import net.paoding.rose.jade.provider.DataAccess;
import net.paoding.rose.jade.provider.SQLInterpreterResult;
import net.paoding.rose.jade.provider.SQLInterpreter;
import net.paoding.rose.jade.provider.Modifier;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * 提供动态: SQL 语句功能的 {@link DataAccess} 实现。
 * <p/>
 * 目前没有使用此类， 推荐使用ExpressSQLInterpreter
 *
 * @author han.liao
 */
public class SimpleNamedParamSQLInterpreter implements SQLInterpreter {

    private static final Pattern NAMED_PARAM_PATTERN = Pattern.compile("(\\:([a-zA-Z0-9_\\.]+))");

    @Override
    // 转换   JDQL 语句为正常的  SQL 语句
    public SQLInterpreterResult interpret(DataSource dataSource, String sql, Modifier modifier,
                                          Map<String, Object> parametersAsMap, Object[] parametersAsArray) {
        return resolveParam(sql, parametersAsMap);
    }

    public SQLInterpreterResult resolveParam(String sql, Map<String, Object> parameters) {

        final List<Object> parametersAsList = new LinkedList<Object>();

        // 匹配符合  :name 格式的参数
        Matcher matcher = NAMED_PARAM_PATTERN.matcher(sql);
        if (!matcher.find()) {
            return null;
        }

        final StringBuilder builder = new StringBuilder();

        int index = 0;

        do {
            // 提取参数名称
            String name = matcher.group(1);
            if (NumberUtils.isDigits(name)) {
                name = matcher.group();
            }

            Object value = null;

            // 解析  a.b.c 类型的名称 
            int find = name.indexOf('.');
            if (find >= 0) {

                // 用  BeanWrapper 获取属性值
                Object bean = parameters.get(name.substring(0, find));
                if (bean != null) {
                    BeanWrapper beanWrapper = new BeanWrapperImpl(bean);
                    value = beanWrapper.getPropertyValue(name.substring(find + 1));
                }

            } else {
                // 直接获取值
                value = parameters.get(name);
            }

            // 拼装查询语句
            builder.append(sql.substring(index, matcher.start()));

            if (value instanceof Collection<?>) {

                // 拼装 IN (...) 的查询条件
                builder.append('(');

                Collection<?> collection = (Collection<?>) value;

                if (collection.isEmpty()) {
                    builder.append("NULL");
                } else {
                    builder.append('?');
                }

                for (int i = 1; i < collection.size(); i++) {
                    builder.append(", ?");
                }

                builder.append(')');

                // 保存参数值
                parametersAsList.addAll(collection);

            } else {
                // 拼装普通的查询条件
                builder.append('?');

                // 保存参数值
                parametersAsList.add(value);
            }

            index = matcher.end();

        } while (matcher.find());

        // 拼装查询语句
        builder.append(sql.substring(index));

        return new SQLInterpreterResult() {

            @Override
            public String getSQL() {
                return builder.toString();
            }

            @Override
            public Object[] getParameters() {
                return parametersAsList.toArray();
            }
        };

    }

}
