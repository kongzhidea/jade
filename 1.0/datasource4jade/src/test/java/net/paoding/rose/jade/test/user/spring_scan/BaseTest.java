package net.paoding.rose.jade.test.user.spring_scan;

import net.paoding.rose.jade.dao.UserDAO;
import net.paoding.rose.jade.model.User;
import net.paoding.rose.jade.util.ConsoleLogger;

import java.util.*;

public class BaseTest {
    protected ConsoleLogger logger = new ConsoleLogger();
    protected UserDAO userDAO;

    protected void _testGet() {
        logger.info(userDAO.getRealName(195));

        User user = userDAO.getUser(195);
        logger.info("bean:" + user);

        List<Integer> ids = new ArrayList<Integer>();
        ids.add(195);
        ids.add(200);

        logger.info("map:" + userDAO.getUserRealNameMap(ids));
        logger.info("map:" + userDAO.getUserRealNameMapRev(ids));

        logger.info("totalCount = " + userDAO.getTotalUserCount());

        user = new User();
//        user.setId(0);
//        user.setUsername("kongzhihui");
        user.setRealname("孔智慧");

        List<User> list = userDAO.getUserList(user);
        logger.info("list,if:" + list);

        User[] array = userDAO.getUserArray(user);
        logger.info("ret.array=" + Arrays.asList(array));

        List<Map<String, Object>> listmap = userDAO.getUserListMap(user);
        logger.info("ret.list.map=" + listmap);

        Set<User> set = userDAO.getUserSet(user);
        logger.info("ret.set=" + set);

        List<Integer> retIdList = userDAO.getUserIdList(user);
        logger.info("ret.list.id=" + retIdList);

        list = userDAO.getUserByRealName("%孔%");
        logger.info("like=" + list);
    }

    protected void _testUpdate(int i) {
        User user = new User();
        user.setRealname("孔_" + i);
        user.setUsername("u_test_" + i);
        user.setCityName("北京");
        user.setCtime(new Date());
        user.setPrivs("p");

        int id = userDAO.addUser(user);
        logger.info("id=" + id);

        user.setId(id);

        user.setPrivs("priv_");
        int updateCount = userDAO.updateUser(user);
        logger.info("updateCount=" + updateCount);
        logger.info("getUser=" + userDAO.getUser(id));

//        int deleteCount = userDAO.deleteUser(id);
//        logger.info("deleteCount=" + deleteCount);
    }
}
