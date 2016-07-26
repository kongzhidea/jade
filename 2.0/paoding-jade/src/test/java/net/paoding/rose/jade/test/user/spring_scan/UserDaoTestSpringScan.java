package net.paoding.rose.jade.test.user.spring_scan;

import net.paoding.rose.jade.dao.UserDAO;
import net.paoding.rose.jade.service.UserService;
import net.paoding.rose.jade.test.user.simple.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UserDaoTestSpringScan extends BaseTest {

    UserService userService;

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath*:jade/applicationContext*.xml",
                "classpath:applicationContext*.xml",
                "classpath:spring-scan/applicationContext*.xml");

        userDAO = context.getBean(UserDAO.class);
//        userDAO = (UserDAO) context.getBean("userDAO");

        userService = context.getBean(UserService.class);
    }

    @Test
    public void testGet() {
        logger.info("userService:" + userService.getUser(195));
        _testGet();

    }
}
