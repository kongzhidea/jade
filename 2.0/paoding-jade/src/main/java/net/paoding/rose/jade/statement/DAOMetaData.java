package net.paoding.rose.jade.statement;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link DAOMetaData} 封装缓存一个DAO接口类本身的一些信息，比如类对象、类常量等等
 *
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
public class DAOMetaData {

    /**
     *
     */
    private final DAOConfig config;

    /**
     * DAO接类
     */
    private final Class<?> daoClass;

    /**
     * 定义在DAO接口上的常量（包含父接口的）
     */
    private final Map<String, Object> constants;

    /**
     * DAO类上的属性
     */
    private final Map<String, Object> attributes;

    /**
     * @param daoClass
     */
    public DAOMetaData(Class<?> daoClass, DAOConfig config) {
        this.daoClass = daoClass;
        this.config = config;
        this.constants = Collections
                .unmodifiableMap(GenericUtils.getConstantFrom(daoClass, true, true));
        this.attributes = new ConcurrentHashMap<String, Object>(4);
    }

    /**
     * 支持本DAO类的基础配置（数据源配置、解析器配置、OR映射配置等等）
     *
     * @return
     */
    public DAOConfig getConfig() {
        return config;
    }

    public Class<?> getDAOClass() {
        return daoClass;
    }

    /**
     * 泛型类型变量在本DAO类中真正的类型
     *
     * @param declaringClass 声明类型变量typeVarName的类
     * @param typeVarName    泛型变量名
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Class resolveTypeVariable(Class<?> declaringClass, String typeVarName) {
        return GenericUtils.resolveTypeVariable(daoClass, declaringClass, typeVarName);
    }

    public Map<String, Object> getConstants() {
        return constants;
    }

    public Object getConstant(String fieldName) {
        return constants.get(fieldName);
    }

    /**
     * 设置挂在DAO上的属性
     *
     * @param name
     * @param value
     */
    public void setAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    /**
     * @param name
     * @return 获取由 {@link #setAttribute(String, Object)} 的属性
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DAOMetaData) {
            DAOMetaData other = (DAOMetaData) obj;
            return daoClass.equals(other.daoClass);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return daoClass.hashCode() * 13;
    }

    @Override
    public String toString() {
        return daoClass.getName();
    }
}
