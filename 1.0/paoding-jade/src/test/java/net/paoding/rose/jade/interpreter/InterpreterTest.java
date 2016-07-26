package net.paoding.rose.jade.interpreter;

import net.paoding.rose.jade.model.User;
import net.paoding.rose.jade.provider.SQLInterpreterResult;
import net.paoding.rose.jade.provider.jdbctemplate.ExpressSQLInterpreter;
import net.paoding.rose.jade.provider.jdbctemplate.SimpleNamedParamSQLInterpreter;
import org.junit.Test;

import java.util.*;

public class InterpreterTest {

    @Test
    public void testSimple() {
        SimpleNamedParamSQLInterpreter parser = new SimpleNamedParamSQLInterpreter();

        User user = new User();
        user.setId(195);
        user.setUsername("kongzhihui");

        // simple
        {
            String sql = "select realname from eby_user where id=:1 and username=:username";

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(":1", 195);
            parameters.put(":id", 195);

            parameters.put(":2", "kongzhihui");
            parameters.put(":username", "kongzhihui");

            SQLInterpreterResult result = parser.resolveParam(sql, parameters);

            System.out.println(result.getSQL());
            System.out.println(Arrays.asList(result.getParameters()));
        }
        // bean
        {
            String sql = "select realname from eby_user where id=:user.id and username=:user.username";

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(":user", user);

            SQLInterpreterResult result = parser.resolveParam(sql, parameters);

            System.out.println(result.getSQL());
            System.out.println(Arrays.asList(result.getParameters()));
        }
        // list
        {
            String sql = "select realname from eby_user where id in :ids";

            Map<String, Object> parameters = new HashMap<String, Object>();
            List<Integer> ids = new ArrayList<Integer>();
            ids.add(1);
            ids.add(2);
            parameters.put(":ids", ids);

            SQLInterpreterResult result = parser.resolveParam(sql, parameters);

            System.out.println(result.getSQL());
            System.out.println(Arrays.asList(result.getParameters()));
        }
        // 不支持数组

    }

    @Test
    public void testExpress() {
        ExpressSQLInterpreter parser = new ExpressSQLInterpreter();

        User user = new User();
        user.setId(195);
        user.setUsername("kongzhihui");

        Map<String, Object> constsMap = new HashMap<String, Object>();
        constsMap.put("FIELD", "username");


        // simple
        {
            String sql = "select realname from eby_user where id=:1 and username=:username";

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(":1", 195);
            parameters.put("id", 195);

            parameters.put(":2", "kongzhihui");
            parameters.put("username", "kongzhihui");

            SQLInterpreterResult result = parser.resolveParam(sql, null, parameters);

            System.out.println("simple=" + result.getSQL());
            System.out.println(Arrays.asList(result.getParameters()));
        }
        // field
        {
            String sql = "select $FIELD from eby_user where id=#(:1) and username= #(:username)";

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(":1", 195);
            parameters.put("id", 195);

            parameters.put(":2", "kongzhihui");
            parameters.put("username", "kongzhihui");

            SQLInterpreterResult result = parser.resolveParam(sql, constsMap, parameters);

            System.out.println("field=" + result.getSQL());
            System.out.println(Arrays.asList(result.getParameters()));
        }
        // bean
        {
            String sql = "select realname from eby_user where id=:user.id and username=:user.username";

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("user", user);

            SQLInterpreterResult result = parser.resolveParam(sql, null, parameters);

            System.out.println("bean=" + result.getSQL());
            System.out.println(Arrays.asList(result.getParameters()));
        }
        // list
        {
            String sql = "select realname from eby_user where id in (:ids)";

            Map<String, Object> parameters = new HashMap<String, Object>();
            List<Integer> ids = new ArrayList<Integer>();
            ids.add(1);
            ids.add(2);
            parameters.put("ids", ids);

            SQLInterpreterResult result = parser.resolveParam(sql, null, parameters);

            System.out.println("list=" + result.getSQL());
            System.out.println(Arrays.asList(result.getParameters()));
        }
        // array
        {
            String sql = "select realname from eby_user where id in (:ids)";

            Map<String, Object> parameters = new HashMap<String, Object>();
            Integer[] ids = new Integer[]{1, 2};
            parameters.put("ids", ids);

            SQLInterpreterResult result = parser.resolveParam(sql, null, parameters);

            System.out.println("array=" + result.getSQL());
            System.out.println(Arrays.asList(result.getParameters()));
        }
        //if
        {
            String sql = "select realname from eby_user where 1=1  #if(:id != 0) {and id=:id }   ";

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("id", 1);

            SQLInterpreterResult result = parser.resolveParam(sql, null, parameters);

            System.out.println("if=" + result.getSQL());
            System.out.println(Arrays.asList(result.getParameters()));
        }
        //for each
        {

        }
        // ## 拼接字符串
        {
            String sql = "select realname from eby_user where id= ##(:id)   ";

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("id", 2);

            SQLInterpreterResult result = parser.resolveParam(sql, null, parameters);

            System.out.println("##=" + result.getSQL());
            System.out.println(Arrays.asList(result.getParameters()));
        }
    }
}
