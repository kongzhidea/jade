package net.paoding.rose.jade.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 把 {@link ShardParam} 标注在 SQL 查询的散表参数上，说明该参数值用于散库 / 散表。
 * 
 * @author han.liao [in355hz@gmail.com]
 */
@Target( { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ShardParam {

    // 匹配所有列
    final String WIDECARD = "*";

    /**
     * 指出这个参数作为 SQL 语句中哪个散表字段。
     * 
     * @return 对应的散表字段
     */
    String name() default WIDECARD;

    /**
     * 指出这个参数值如何计算。
     * 
     * @return 计算参数值
     */
    String value();
}
