###代码出自[https://github.com/paoding-code/paoding-rose](https://github.com/paoding-code/paoding-rose)
***

##需要在spring中配置的类
* SpringDataSourceFactory 在applicationContext.xml中配置 dataSource
* DataAccessFactoryAdapter  对dataSourceFactory的封装， 获取DataAccess
* interpreter 需要在xml中配置，如 SpringInterpreterFactory， 默认会加载系统sql解析器。 如果自定义解析器 最好order值设置为负数，让其先执行。
* DefaultRowMapperFactory， 获取rowMapper
* 在war项目的applicationContext.xml中增加数据源定义：
     * demo
      ```
​<!-- 数据源配置 dbcp -->
<bean id="jade.dataSource.com.chen.dao" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
    <property name="driverClassName"value="com.mysql.jdbc.Driver">    </property>
    <property name="url"value="jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&amp;characterEncoding=utf-8">    </property>
    <property name="username"value="test"></property>
    <property name="password"value="test"></property><!-- 运行判断连接超时任务的时间间隔，单位为毫秒，默认为-1，即不执行任务。 -->
    <property name="timeBetweenEvictionRunsMillis"value="3600000"></property><!-- 连接的超时时间，默认为半小时。 -->
    <property name="minEvictableIdleTimeMillis"value="3600000"></property>
</bean>
     ```
    * MasterSlaverDataSource 可以配置主从数据源
    * 这里假设了mysql已经安装在本地了，用户名为test，密码为test。
    * jade约定 先取 jade.dataSource.$catalog，  如果dao中没有配置catalog，则默认为 dao的类名。
    * jade约定了bean的id为jade.dataSource.classPackageName。
    * jade约定了这个bean的有效范围为classPackageName所有的DAO。
    * jade约定了除非有专门的定义，所有的子目录也受bean上的classpackageName所影响。
    * 最后取jade.dataSource和dataSource

##使用方式，见test里面的样例
*
* mapper解析
    * ReturnGeneratedKeys 返回自增Id， 返回值为primitive（如int）或者Wrapper（如Integer）类型都可以
    * 批量添加 无法生成自增Id
    * 返回单行
        * int,long,BigDecimal
        * map
        * Date
    * 返回多行
        * list&lt;int&gt; ..  如果不存在则返回空list
        * list&lt;map&gt;
        * set
        * array  如果不存在则返回空 数组

##实现功能
* spring配置：支持， 使用JadeScannerConfigurer，只扫描 DAO注解的接口。 需要自行配置 basePackage，如果扫描多个路径，用分号隔开。
    * 在代码中 默认已经配置了从spring中获取 dataAccessFactory，rowMapperFactory，interpreterFactory，只需要扫描classpath*:jade/applicationContext*.xml路径即可。
* 扫描dao的配置：支持
* 分库：支持spring配置主从分离， 不支持从zk中配置
* 分表：不支持















