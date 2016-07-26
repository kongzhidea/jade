package net.paoding.rose.jade.util;

import net.paoding.rose.jade.model.User;
import net.paoding.rose.jade.statement.GenericUtils;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

public class GenericUtilTest {

    @Test
    public void testGeneric() {
        // 输出所有常量
        System.out.println("输出所有常量");
        Map<String, ?> constants = GenericUtils.getConstantFrom(User.class, true, true);
        System.out.println(constants);
    }
}
