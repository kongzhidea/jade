package net.paoding.rose.jade.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RowHandler {

    /**
     * 指定自己设置的 rowMapper 类；rowMapper类应该做到无状态。
     *
     * @return
     */
    Class<? extends RowMapper> rowMapper() default ByDefault.class;

    /**
     * 这是一个检查开关,默认为true；
     * <p/>
     * true代表如果不是所有列都被映射给一个 Bean 的属性，抛出异常。
     *
     * @return
     */
    boolean checkColumns() default true;

    /**
     * 这是一个检查开关，默认为false; true代表如果不是每一个bean 属性都设置了SQL查询结果的值，抛出异常。
     *
     * @return
     */
    boolean checkProperties() default false;

    class ByDefault implements RowMapper {

        @Override
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return null;
        }

    }
}
