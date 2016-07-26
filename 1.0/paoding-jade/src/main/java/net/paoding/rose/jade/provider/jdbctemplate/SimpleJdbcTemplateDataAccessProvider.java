package net.paoding.rose.jade.provider.jdbctemplate;

import net.paoding.rose.jade.datasource.DataSourceFactory;
import net.paoding.rose.jade.datasource.SpringDataSourceFactory;
import net.paoding.rose.jade.provider.AbstractDataAccessProvider;
import net.paoding.rose.jade.provider.DataAccess;
import net.paoding.rose.jade.provider.SQLInterpreter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

/**
 * 不基于spring
 */
public class SimpleJdbcTemplateDataAccessProvider extends AbstractDataAccessProvider {


    // 在父类中 setDataSourceFactory， 测试可以使用 SimpleDataSourceFactory
    @Override
    protected DataSourceFactory createDataSourceFactory() {
        return null;
    }

    @Override
    protected final DataAccess createDataAccess(DataSource dataSource) {
        JdbcTemplateDataAccess dataAccess = createEmptyJdbcTemplateDataAccess();
        dataAccess.setDataSource(dataSource);
        dataAccess.setInterpreters(findSQLInterpreters());
        return dataAccess;
    }

    protected JdbcTemplateDataAccess createEmptyJdbcTemplateDataAccess() {
        return new JdbcTemplateDataAccess();
    }

    protected SQLInterpreter[] findSQLInterpreters() {
        ExpressSQLInterpreter parser = new ExpressSQLInterpreter();
        return new SQLInterpreter[]{parser};
    }
}
