package net.paoding.rose.jade.statement;

import org.apache.commons.lang.ClassUtils;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import net.paoding.rose.jade.annotation.ReturnGeneratedKeys;

/**
 * @see ReturnGeneratedKeys
 */
public abstract class DynamicReturnGeneratedKeys {

    /**
     * 是否要启动 return generated keys机制
     *
     * @param runtime
     */
    public abstract boolean shouldReturnGerneratedKeys(StatementRuntime runtime);

    /**
     * 检查DAO返回的类型是否合格
     *
     * @param returnType DAO方法的返回类型（如果方法声明的返回类型是泛型，框架会根据上下文信息解析为运行时实际应该返回的真正类型)
     * @throws InvalidDataAccessApiUsageException DAO方法的返回类型不合格
     */
    public void checkMethodReturnType(Class<?> returnType, StatementMetaData metaData) {
        returnType = ClassUtils.primitiveToWrapper(returnType);
        if (returnType != void.class && !Number.class.isAssignableFrom(returnType)) {
            throw new InvalidDataAccessApiUsageException(
                    "error return type, only support int/long/double/float/void type for method with @ReturnGeneratedKeys:"
                            + metaData.getMethod());
        }
    }
}
