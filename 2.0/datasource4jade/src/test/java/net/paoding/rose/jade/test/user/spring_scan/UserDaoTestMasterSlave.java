package net.paoding.rose.jade.test.user.spring_scan;

import net.paoding.rose.jade.dao.UserDAO;
import net.paoding.rose.jade.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * UserDAO 中设置 catalog，从而删除本地配置的数据源
 */
public class UserDaoTestMasterSlave extends BaseTest {

    UserService userService;

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath*:jade/applicationContext*.xml",
                "classpath:spring-scan/applicationContext*.xml");

        userDAO = context.getBean(UserDAO.class);
//        userDAO = (UserDAO) context.getBean("userDAO");

        userService = context.getBean(UserService.class);
    }

    @Test
    public void testGet() {
        for (int i = 0; i < 5; i++) {
            logger.info("userService:" + userService.getUser(195));
        }
        _testGet();

    }

    @Test
    public void testUpdate() {
        for (int i = 0; i < 5; i++) {
            _testUpdate(i);
        }
    }
}
