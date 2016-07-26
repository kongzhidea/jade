package net.paoding.rose.jade.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import net.paoding.rose.jade.rowmapper.RowMapperFactory;
import net.paoding.rose.jade.statement.StatementMetaData;

/**
 * @author 王志亮 [qieqie.wang@gmail.com]
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RowHandler {

    /**
     * 指定自己设置的 rowMapper 类；rowMapper类应该做到无状态。<p>
     */
    @SuppressWarnings("rawtypes") Class<? extends RowMapper> rowMapper() default NotSettingRowMapper.class;

    /**
     * 通过自定义的 {@link RowMapperFactory} 指定自己设置的 rowMapper 类；返回的rowMapper类应该做到无状态。<p>
     */
    Class<? extends RowMapperFactory> rowMapperFactory() default NotSettingRowMapperFactory.class;

    /**
     * 这是一个检查开关,默认为true；
     * <p/>
     * true代表如果不是所有列都被映射给一个 Bean 的属性，抛出异常。
     */
    boolean checkColumns() default true;

    /**
     * 这是一个检查开关，默认为false; true代表如果不是每一个bean 属性都设置了SQL查询结果的值，抛出异常。
     */
    boolean checkProperties() default false;

    class NotSettingRowMapper implements RowMapper<Object> {

        @Override
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return null;
        }

    }

    class NotSettingRowMapperFactory implements RowMapperFactory {

        @Override
        public RowMapper<?> getRowMapper(StatementMetaData metaData) {
            return null;
        }

    }

}
