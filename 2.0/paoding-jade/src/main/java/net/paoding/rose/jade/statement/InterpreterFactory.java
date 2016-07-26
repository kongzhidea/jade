package net.paoding.rose.jade.statement;

/**
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * 
 */
public interface InterpreterFactory {

    Interpreter[] getInterpreters(StatementMetaData metaData);
}
