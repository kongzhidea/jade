package net.paoding.rose.jade.route.parse;

import net.paoding.rose.jade.route.RoutingDescriptor;

/**
 * 存放: XceDataSource 的配置信息。
 */
public interface RoutingConfigurator {

    /**
     * 返回数据表的配置信息。
     *
     * @param catalog - 模块名称
     * @param name    - 数据表名称
     * @return 数据表的配置信息
     */
    RoutingDescriptor getDescriptor(String catalog, String name);
}
