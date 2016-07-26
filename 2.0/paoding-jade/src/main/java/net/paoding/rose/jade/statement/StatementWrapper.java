package net.paoding.rose.jade.statement;

/**
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 *
 */
public interface StatementWrapper extends Statement {

    /**
     * 
     * @param statement
     */
    void setStatement(Statement statement);

    /**
     * 
     * @return
     */
    Statement getStatement();
}
