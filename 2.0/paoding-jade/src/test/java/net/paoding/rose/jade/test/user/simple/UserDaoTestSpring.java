package net.paoding.rose.jade.test.user.simple;

import net.paoding.rose.jade.dao.UserDAO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UserDaoTestSpring extends BaseTest {


    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath*:jade/applicationContext*.xml",
                "classpath:applicationContext*.xml",
                "classpath:spring-simple/applicationContext*.xml");

        userDAO = context.getBean("userDAO", UserDAO.class);
    }

    @Test
    public void testGet() {
        _testGet();
    }

    @Test
    public void testUpdate() {
        _testUpdate();
    }
}
