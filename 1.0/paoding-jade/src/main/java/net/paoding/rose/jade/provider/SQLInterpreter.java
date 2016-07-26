package net.paoding.rose.jade.provider;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.core.annotation.Order;

/**
 * 可用 {@link Order}来调节优先级，根据 {@link Order} 语义，值越小越有效；
 * <p>
 * 如果没有标注 {@link Order} 使用默认值0。
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 * 
 */
//按Spring语义规定，Order值越高该解释器越后执行
@Order(0)
public interface SQLInterpreter {

    /**
     * 
     * @param dataSource
     * @param sql
     * @param modifier
     * @param parametersAsMap
     * @param parametersAsArray 可以为null
     * @return
     */
    SQLInterpreterResult interpret(DataSource dataSource, String sql, Modifier modifier,
            Map<String, Object> parametersAsMap, Object[] parametersAsArray);

}
