package net.paoding.rose.jade.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.paoding.rose.jade.statement.DynamicReturnGeneratedKeys;
import net.paoding.rose.jade.statement.StatementRuntime;

/**
 * 
 * 将 {@link ReturnGeneratedKeys} 声明在insert语句的方法上，表示返回的是插入的id。
 * 
 * <pre>
 * 1、在@SQL上声明注解 @ReturnGeneratedKeys
 * 2、方法返回值改为欲返回的数值类型，比如long、int等
 * 例子：
 * 
 * &#064;ReturnGeneratedKeys
 * &#064;SQL(&quot;insert into role(id, name) values(myseq.nextal, :1)&quot;)
 * public long save(String name);
 * </pre>
 * 
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReturnGeneratedKeys {

    Class<? extends DynamicReturnGeneratedKeys>value() default Yes.class;

    static class Yes extends DynamicReturnGeneratedKeys {

        @Override
        public boolean shouldReturnGerneratedKeys(StatementRuntime runtime) {
            return true;
        }

    }
}
