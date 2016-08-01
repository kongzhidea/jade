package net.paoding.rose.jade.route;

import com.meidusa.amoeba.parser.dbobject.Column;
import com.meidusa.amoeba.parser.dbobject.Table;
import net.paoding.rose.jade.route.instance.Router;

public class RoutingInfo {

    private Table byTable;

    private RoutingDescriptor descriptor;

    public RoutingInfo(Table table, RoutingDescriptor descriptor) {
        this.byTable = table;
        this.descriptor = descriptor;
    }

    public Table getByTable() {
        return byTable;
    }

    public void setByTable(Table byTable) {
        this.byTable = byTable;
    }

    public RoutingDescriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(RoutingDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public Router getDbRouter() {
        return descriptor.getDbRouter();
    }

    public Router getTableRouter() {
        return descriptor.getTableRouter();
    }

    private Column dbRouterColumn;

    public Column getDbRouterColumn() {
        if (dbRouterColumn != null) {
            return dbRouterColumn;
        }
        Router dbRouter = getDbRouter();
        if (dbRouter == null) {
            return null;
        }

        String columnName = dbRouter.getColumn();

        if (columnName != null) {
            // 保存匹配的数据列
            Column columnForDBPartition = new Column();
            columnForDBPartition.setName(columnName.toUpperCase());
            columnForDBPartition.setTable(byTable);
            this.dbRouterColumn = columnForDBPartition;
        }
        return dbRouterColumn;
    }

    private Column tableRouterColumn;

    public Column getTableRouterColumn() {
        if (tableRouterColumn != null) {
            return tableRouterColumn;
        }
        Router tableRouter = getTableRouter();
        if (tableRouter == null) {
            return null;
        }

        String columnName = tableRouter.getColumn();

        if (columnName != null) {
            // 保存匹配的数据列
            Column tableRouterColumn = new Column();
            tableRouterColumn.setName(columnName.toUpperCase());
            tableRouterColumn.setTable(byTable);
            this.tableRouterColumn = tableRouterColumn;
        }
        return tableRouterColumn;
    }
}