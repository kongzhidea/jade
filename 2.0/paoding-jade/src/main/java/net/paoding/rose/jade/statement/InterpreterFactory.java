package net.paoding.rose.jade.statement;

/**
 * 
 * @author ÍõÖ¾ÁÁ [qieqie.wang@gmail.com]
 * 
 */
public interface InterpreterFactory {

    Interpreter[] getInterpreters(StatementMetaData metaData);
}
