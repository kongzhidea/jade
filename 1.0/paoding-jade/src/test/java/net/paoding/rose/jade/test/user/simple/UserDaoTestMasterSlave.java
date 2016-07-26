package net.paoding.rose.jade.test.user.simple;

import net.paoding.rose.jade.core.JadeDaoFactoryBean;
import net.paoding.rose.jade.dao.UserDAO;
import net.paoding.rose.jade.datasource.SingleDataSourceFactory;
import net.paoding.rose.jade.datasource.instances.MasterSlaverDataSource;
import net.paoding.rose.jade.model.User;
import net.paoding.rose.jade.provider.jdbctemplate.SimpleJdbcTemplateDataAccessProvider;
import net.paoding.rose.jade.util.ConsoleLogger;
import net.paoding.rose.jade.util.Consts;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class UserDaoTestMasterSlave extends BaseTest {

    @Before
    public void init() {

        SingleDataSourceFactory dataSourceFactory = new SingleDataSourceFactory();

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Consts.driverClassName);
        dataSource.setUrl(Consts.url);
        dataSource.setUsername(Consts.username);
        dataSource.setPassword(Consts.password);

        DriverManagerDataSource dataSource2 = new DriverManagerDataSource();
        dataSource2.setDriverClassName(Consts.driverClassName);
        dataSource2.setUrl(Consts.url_stat);
        dataSource2.setUsername(Consts.username);
        dataSource2.setPassword(Consts.password);


        //MasterSlaverDataSource  可是设置主从， 需要和 SQLThreadLocalWrapper 一起使用
        MasterSlaverDataSource masterSlaverDataSource = new MasterSlaverDataSource();
        masterSlaverDataSource.getMasters().add(dataSource);
        masterSlaverDataSource.getSlavers().add(dataSource);
        masterSlaverDataSource.getSlavers().add(dataSource2);

        dataSourceFactory.setDataSource(masterSlaverDataSource);

        SimpleJdbcTemplateDataAccessProvider provider = new SimpleJdbcTemplateDataAccessProvider();
        provider.setDataSourceFactory(dataSourceFactory);


        JadeDaoFactoryBean<UserDAO> factoryBean = new JadeDaoFactoryBean<UserDAO>();
        factoryBean.setDaoClass(UserDAO.class);
        factoryBean.setDataAccessProvider(provider);
        userDAO = factoryBean.getObject();
    }

    @Test
    public void testGet() {
        _testGet();
    }
}
