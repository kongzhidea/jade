package net.paoding.rose.jade.provider;

/**
 * 定义: DataAccess 的供应者接口。
 *
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
public interface DataAccessProvider {
    String springBeanName = "jade.dataAccessProvider";
    /**
     * 创建一个: {@link daoClass} 对象。
     *
     * @param daoClass -
     * @return DataAccess 对象
     */
    public DataAccess createDataAccess(Class<?> daoClass);
}
