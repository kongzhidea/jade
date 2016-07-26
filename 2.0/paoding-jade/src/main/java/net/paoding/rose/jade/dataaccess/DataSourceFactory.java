package net.paoding.rose.jade.dataaccess;

import java.util.Map;

import net.paoding.rose.jade.context.spring.SpringDataSourceFactory;
import net.paoding.rose.jade.statement.StatementMetaData;
import net.paoding.rose.jade.statement.StatementRuntime;

/**
 * Jade框架的数据源接口，应用程序通过配置合适的DataSourceFactory实现，框架才能将SQL语句发送到正确的数据库上。
 * <p>
 *
 * @see SpringDataSourceFactory
 * @author 王志亮 [qieqie.wang@gmail.com]
 */
public interface DataSourceFactory {

    /**
     * 框架调用此方法为一次DAO执行设置相应的数据源，当你的代码调用一次DAO方法时，本方法将对应被框架调用一次。
     * <p>
     *
     * 框架把每一个不同的DAO方法抽象为一个唯一的metaData对象，当本方法被调用时候，
     * 框架会传入DAO方法对应的metaData参数到本方法中 。
     * 同时框架也会将调用此DAO方法时候的参数传入到本方法中，以让你有机会了解到当前的具体运行时。
     * 当然，你如果可以不理会metadata和runtime参数，如果你不敢兴趣的话。有些系统可能只有一个数据源，此时就是如此。
     * <p>
     * 参数说明:<br>
     * runtime默认包含调用DAO方法时的参数，你可以通过该参数的名称获取之(如<code>@SQLParam("id")</code>
     * 中的"id") ，同时也支持使用":1"、":2"的方式获取第一个、第二个参数。
     * <p>
     *
     * @param metaData 正在执行的DAO方法
     * @param attributes DAO方法执行时的属性，即 {@link StatementRuntime#getAttributes()}
     * @return
     */
    DataSourceHolder getHolder(StatementMetaData metaData, Map<String, Object> attributes);
}
