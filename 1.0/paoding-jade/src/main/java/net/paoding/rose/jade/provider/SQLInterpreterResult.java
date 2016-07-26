package net.paoding.rose.jade.provider;

/**
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 * 
 */
public interface SQLInterpreterResult {

    /**
     * 
     * @return
     */
    String getSQL();

    /**
     * 
     * @return
     */
    Object[] getParameters();
}
