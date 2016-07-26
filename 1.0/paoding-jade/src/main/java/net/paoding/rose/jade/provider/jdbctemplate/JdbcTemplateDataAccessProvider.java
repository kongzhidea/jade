package net.paoding.rose.jade.provider.jdbctemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import javax.sql.DataSource;

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

/**
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
public class JdbcTemplateDataAccessProvider extends AbstractDataAccessProvider implements
        ApplicationContextAware {

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    protected DataSourceFactory createDataSourceFactory() {
        Map<?, ?> beansOfType = applicationContext.getBeansOfType(DataSourceFactory.class);
        if (beansOfType.size() > 1) {
            throw new NoSuchBeanDefinitionException(DataSourceFactory.class,
                    "expected single bean but found " + beansOfType.size());
        } else if (beansOfType.size() == 1) {
            return (DataSourceFactory) beansOfType.values().iterator().next();
        }
        SpringDataSourceFactory dataSourceFactory = new SpringDataSourceFactory();
        dataSourceFactory.setApplicationContext(applicationContext);
        return dataSourceFactory;
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
        @SuppressWarnings("unchecked")
        Collection<SQLInterpreter> interpreters = this.applicationContext.getBeansOfType(
                SQLInterpreter.class).values();
        SQLInterpreter[] arrayInterpreters = interpreters.toArray(new SQLInterpreter[0]);
        Arrays.sort(arrayInterpreters, new Comparator<SQLInterpreter>() {

            @Override
            public int compare(SQLInterpreter thees, SQLInterpreter that) {
                Order thessOrder = thees.getClass().getAnnotation(Order.class);
                Order thatOrder = that.getClass().getAnnotation(Order.class);
                int thessValue = thessOrder == null ? 0 : thessOrder.value();
                int thatValue = thatOrder == null ? 0 : thatOrder.value();
                return thessValue - thatValue;
            }

        });
        return arrayInterpreters;
    }
}
