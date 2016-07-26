package net.paoding.rose.jade.interpreter;

import net.paoding.rose.jade.dao.UserDAO;
import net.paoding.rose.jade.dataaccess.DataAccessFactory;
import net.paoding.rose.jade.dataaccess.DataAccessFactoryAdapter;
import net.paoding.rose.jade.dataaccess.DataSourceFactory;
import net.paoding.rose.jade.dataaccess.datasource.SimpleDataSourceFactory;
import net.paoding.rose.jade.model.User;
import net.paoding.rose.jade.rowmapper.DefaultRowMapperFactory;
import net.paoding.rose.jade.rowmapper.RowMapperFactory;
import net.paoding.rose.jade.statement.*;
import net.paoding.rose.jade.util.Consts;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.lang.reflect.Method;
import java.util.*;

public class InterpreterTest {

    @Test
    public void testExpress() throws Exception {
        InterpreterFactory interpreterFactory = new DefaultInterpreterFactory();


        SimpleDataSourceFactory dataSourceFactory = new SimpleDataSourceFactory();

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Consts.driverClassName);
        dataSource.setUrl(Consts.url);
        dataSource.setUsername(Consts.username);
        dataSource.setPassword(Consts.password);
        dataSourceFactory.setDataSource(dataSource);


        DataAccessFactory dataAccessFactory = new DataAccessFactoryAdapter(dataSourceFactory);


        RowMapperFactory rowMapperFactory = new DefaultRowMapperFactory();

        DAOConfig daoConfig = new DAOConfig(dataAccessFactory, rowMapperFactory, interpreterFactory, null);
        DAOMetaData daoMetaData = new DAOMetaData(UserDAO.class, daoConfig);

        Method method = UserDAO.class.getMethod("testSQL", int.class);
        StatementMetaData statementMetaData = new StatementMetaData(daoMetaData, method);

        List<Integer> ids = new ArrayList<Integer>();
        ids.add(195);

        User user = new User();
        user.setId(195);
        user.setUsername("kongzhihui");

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(":1", 195);
        parameters.put("table","user");
        parameters.put("username","'kongzhihui'");
        parameters.put("id","195");


        StatementRuntimeImpl runtime = new StatementRuntimeImpl(statementMetaData, parameters);


        Interpreter[] interpreters = interpreterFactory.getInterpreters(statementMetaData);

        Interpreter interpreter = interpreters[0];

        interpreter.interpret(runtime);

        System.out.println(runtime);
    }
}
