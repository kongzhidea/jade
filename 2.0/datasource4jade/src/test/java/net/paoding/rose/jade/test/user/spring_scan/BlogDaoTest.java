package net.paoding.rose.jade.test.user.spring_scan;

import net.paoding.rose.jade.dao.BlogDAO;
import net.paoding.rose.jade.dao.UserDAO;
import net.paoding.rose.jade.model.Blog;
import net.paoding.rose.jade.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.List;

/**
 * BlogDao 中设置 catalog，从而删除本地配置的数据源
 * <p/>
 * 配置散表信息
 */
public class BlogDaoTest {

    BlogDAO blogDAO;

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath*:jade/applicationContext*.xml",
                "classpath:spring-scan/applicationContext*.xml");

        blogDAO = context.getBean(BlogDAO.class);
    }

    @Test
    public void testGet() {

        Blog blog = blogDAO.getBlog(1, 4);
        System.out.println(blog);
    }

    @Test
    public void testAdd() {
        for (int i = 1; i <= 10; i++) {
            Blog blog = new Blog();
            blog.setUserId(i);
            blog.setContent("c" + i);
            blog.setCtime(new Date());

            blogDAO.addBlog(blog.getUserId(), blog);
        }
    }
}
