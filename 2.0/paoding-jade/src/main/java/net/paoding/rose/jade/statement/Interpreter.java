package net.paoding.rose.jade.statement;

import org.springframework.core.annotation.Order;

/**
 * 可用 {@link Order}来调节优先级，根据 {@link Order} 语义，值越小越优先，值越大越后；
 * <p>
 * 如果没有标注 {@link Order} 使用默认值0。
 * 
 * 从实践看，jade插件的解析器一般应该设置为负数，以优先于系统解析器。
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 * 
 */
//按Spring语义规定，根据 {@link Order} 语义，值越小越优先，值越大越后；
@Order(0)
public interface Interpreter {

    /**
     * 
     * @param runtime
     */
    void interpret(StatementRuntime runtime);

}
