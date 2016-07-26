package net.paoding.rose.jade.statement;

/**
 * @author 王志亮 [qieqie.wang@gmail.com]
 */
public interface AfterInvocationCallback {

    /**
     * @param runtime     运行时
     * @param returnValue DAO方法的返回值
     * @return 改变后的返回值
     */
    public Object execute(StatementRuntime runtime, Object returnValue);
}
