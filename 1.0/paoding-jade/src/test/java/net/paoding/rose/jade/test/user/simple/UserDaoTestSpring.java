package net.paoding.rose.jade.test.user.simple;

import net.paoding.rose.jade.core.JadeDaoFactoryBean;
import net.paoding.rose.jade.dao.UserDAO;
import net.paoding.rose.jade.model.User;
import net.paoding.rose.jade.provider.DataAccessProvider;
import net.paoding.rose.jade.util.ConsoleLogger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;

public class UserDaoTestSpring extends BaseTest {


    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath*:jade/applicationContext*.xml",
                "classpath:applicationContext*.xml",
                "classpath:spring-simple/applicationContext*.xml");

        // 获取方式1
//        DataAccessProvider provider = context.getBean("jade.dataAccessProvider", DataAccessProvider.class);
//        JadeDaoFactoryBean<UserDAO> factoryBean = new JadeDaoFactoryBean<UserDAO>();
//        factoryBean.setDaoClass(UserDAO.class);
//        factoryBean.setDataAccessProvider(provider);
//        userDAO = factoryBean.getObject();

        // 获取方式2
//        userDAO =  context.getBean(UserDAO.class);
        userDAO = (UserDAO) context.getBean("userDAO");
    }

    @Test
    public void testGet() {
        _testGet();
    }
}
