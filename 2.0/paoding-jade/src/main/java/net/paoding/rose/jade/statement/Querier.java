package net.paoding.rose.jade.statement;

import net.paoding.rose.jade.annotation.SQLType;

/**
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * 
 */
public interface Querier {

    Object execute(SQLType sqlType, StatementRuntime... runtimes);

}
