package net.paoding.rose.jade.test.user.simple;

import net.paoding.rose.jade.core.Identity;
import net.paoding.rose.jade.core.JadeDaoFactoryBean;
import net.paoding.rose.jade.dao.UserDAO;
import net.paoding.rose.jade.datasource.SingleDataSourceFactory;
import net.paoding.rose.jade.model.User;
import net.paoding.rose.jade.provider.jdbctemplate.SimpleJdbcTemplateDataAccessProvider;
import net.paoding.rose.jade.util.ConsoleLogger;
import net.paoding.rose.jade.util.Consts;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.*;

public class UserDaoTest extends BaseTest {

    @Before
    public void init() {

        SingleDataSourceFactory dataSourceFactory = new SingleDataSourceFactory();

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Consts.driverClassName);
        dataSource.setUrl(Consts.url);
        dataSource.setUsername(Consts.username);
        dataSource.setPassword(Consts.password);


        dataSourceFactory.setDataSource(dataSource);

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

    @Test
    public void testUpdate() {
        _testUpdate();
    }

    @Test
    public void testAddList() {
        List<User> list = new ArrayList<User>();
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setRealname("孔_" + i);
            user.setUsername("u_test_" + i);
            user.setCityName("北京");
            user.setCtime(new Date());
            user.setPrivs("p");
            list.add(user);
        }
        int[] ids = userDAO.addUserList(list);
        for (int id : ids) {
            System.out.println(id);
        }
    }
}