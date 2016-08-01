package net.paoding.rose.jade.route;

import net.paoding.rose.jade.route.instance.Router;

/**
 * 实现包含路由的散表配置项。
 */
public class RouterDescriptor extends DirectRoutingDescriptor {

    protected Router dbRouter, tableRouter;

    /**
     * 创建包含路由的散表配置项。
     *
     * @param dbName - 数据源名称
     */
    public RouterDescriptor(String dbName) {
        super(dbName);
    }

    @Override
    public Router getDbRouter() {
        return dbRouter;
    }

    /**
     * 设置散库的路由。
     *
     * @param dbRouter - 散库的路由
     */
    public void setDbRouter(Router dbRouter) {
        this.dbRouter = dbRouter;
    }

    @Override
    public Router getTableRouter() {
        return tableRouter;
    }

    /**
     * 设置散表的路由。
     *
     * @param tableRouter - 散表的路由
     */
    public void setTableRouter(Router tableRouter) {
        this.tableRouter = tableRouter;
    }
}
