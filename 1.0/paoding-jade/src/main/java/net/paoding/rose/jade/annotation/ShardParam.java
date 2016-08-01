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
@Target( { ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ShardParam {

    String value() default "";
}
