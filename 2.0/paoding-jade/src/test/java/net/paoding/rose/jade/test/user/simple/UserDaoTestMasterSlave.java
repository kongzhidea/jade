package net.paoding.rose.jade.test.user.simple;

import net.paoding.rose.jade.context.spring.JadeFactoryBean;
import net.paoding.rose.jade.dao.UserDAO;
import net.paoding.rose.jade.dataaccess.DataAccessFactory;
import net.paoding.rose.jade.dataaccess.DataAccessFactoryAdapter;
import net.paoding.rose.jade.dataaccess.datasource.MasterSlaveDataSourceFactory;
import net.paoding.rose.jade.dataaccess.datasource.SimpleDataSourceFactory;
import net.paoding.rose.jade.rowmapper.DefaultRowMapperFactory;
import net.paoding.rose.jade.rowmapper.RowMapperFactory;
import net.paoding.rose.jade.statement.DefaultInterpreterFactory;
import net.paoding.rose.jade.statement.InterpreterFactory;
import net.paoding.rose.jade.util.Consts;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class UserDaoTestMasterSlave extends BaseTest {

    @Before
    public void init() {

        InterpreterFactory interpreterFactory = new DefaultInterpreterFactory();


        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Consts.driverClassName);
        dataSource.setUrl(Consts.url);
        dataSource.setUsername(Consts.username);
        dataSource.setPassword(Consts.password);

        List<DataSource> slaves = new ArrayList<DataSource>();
        slaves.add(dataSource);

        MasterSlaveDataSourceFactory masterSlaveDataSourceFactory =
                new MasterSlaveDataSourceFactory(dataSource, slaves, false);

        DataAccessFactory dataAccessFactory = new DataAccessFactoryAdapter(masterSlaveDataSourceFactory);


        RowMapperFactory rowMapperFactory = new DefaultRowMapperFactory();

        JadeFactoryBean jadeFactoryBean = new JadeFactoryBean();
        jadeFactoryBean.setDataAccessFactory(dataAccessFactory);
        jadeFactoryBean.setInterpreterFactory(interpreterFactory);
        jadeFactoryBean.setObjectType(UserDAO.class);
        jadeFactoryBean.setRowMapperFactory(rowMapperFactory);
        jadeFactoryBean.setStatementWrapperProvider(null);

        userDAO = (UserDAO) jadeFactoryBean.getObject();
    }

    @Test
    public void testGet() {
        _testGet();
    }
}
