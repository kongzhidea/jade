/*
 * Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License i distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.paoding.rose.jade.core;

import net.paoding.rose.jade.annotation.ReturnGeneratedKeys;
import net.paoding.rose.jade.annotation.SQLParam;
import net.paoding.rose.jade.provider.DataAccess;
import net.paoding.rose.jade.provider.Modifier;
import net.paoding.rose.jade.util.NumberUtil;
import org.apache.commons.lang.ClassUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现 INSERT / UPDATE / DELETE / REPLACE等更新类型语句。
 *
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
public class UpdateOperation implements JdbcOperation {

    private final String sql;

    private final SQLParam[] sqlParamAnnotations;

    private final Class<?> returnType;

    private final Modifier modifier;

    private final DataAccess dataAccess;

    public UpdateOperation(DataAccess dataAccess, String sql, Modifier modifier) {
        this.dataAccess = dataAccess;
        this.sql = sql;
        this.modifier = modifier;
        this.returnType = modifier.getReturnType();
        this.sqlParamAnnotations = modifier.getParameterAnnotations(SQLParam.class);
    }

    @Override
    public Modifier getModifier() {
        return modifier;
    }

    @Override
    public Object execute(Map<String, Object> parameters) {
        if (parameters.get(":1") instanceof List<?>) {
            Class<?> returnType = modifier.getReturnType();
            if (returnType != void.class && returnType != int[].class
                    && returnType != Integer[].class && returnType != int.class
                    && returnType != Integer.class) {
                throw new IllegalArgumentException("error return type for batch update.");
            }
            // 批量执行查询
            return executeBatch(dataAccess, parameters);
        } else {
            // 单个执行查询
            return executeSignle(dataAccess, parameters, returnType);
        }
    }

    private Object executeBatch(DataAccess dataAccess, Map<String, Object> parameters) {

        List<?> list = (List<?>) parameters.get(":1");

        int[] updatedArray;

        if (true) {
            List<Map<String, Object>> parametersList = new ArrayList<Map<String, Object>>(list
                    .size());
            for (Object arg : list) {

                HashMap<String, Object> clone = new HashMap<String, Object>(parameters);

                // 更新执行参数
                clone.put(":1", arg);
                if (this.sqlParamAnnotations[0] != null) {
                    clone.put(this.sqlParamAnnotations[0].value(), arg);
                }
                parametersList.add(clone);
            }
            updatedArray = dataAccess.batchUpdate(sql, modifier, parametersList);
        } else {
            // 批量执行查询
            int index = 0;
            updatedArray = new int[list.size()];
            for (Object arg : list) {

                HashMap<String, Object> clone = new HashMap<String, Object>(parameters);

                // 更新执行参数
                clone.put(":1", arg);
                if (this.sqlParamAnnotations[0] != null) {
                    clone.put(this.sqlParamAnnotations[0].value(), arg);
                }
                updatedArray[index] = (Integer) executeSignle(dataAccess, clone, int.class);

                index++;
            }
        }
        Class<?> batchReturnClazz = modifier.getReturnType();
        if (batchReturnClazz == int[].class) {
            return updatedArray;
        }
        if (batchReturnClazz == Integer[].class) {
            Integer[] ret = new Integer[updatedArray.length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = updatedArray[i];
            }
            return updatedArray;
        }
        if (batchReturnClazz == void.class) {
            return null;
        }
        if (batchReturnClazz == int.class || batchReturnClazz == Integer.class) {
            int updated = 0;
            for (int i = 0; i < updatedArray.length; i++) {
                updated += updatedArray[i];
            }
            return updated;
        }

        return null;
    }

    private Object executeSignle(DataAccess dataAccess, Map<String, Object> parameters,
                                 Class<?> returnType) {

        // 自增ID
        ReturnGeneratedKeys genKey = modifier.getAnnotation(ReturnGeneratedKeys.class);
        if (genKey != null) {
            // 执行 INSERT 查询，返回自增ID
            Number number = dataAccess.insertReturnId(sql, modifier, parameters);

            return NumberUtil.convertNumberToTargetClass(number, returnType);
        }

        if (returnType == Identity.class) {
            // 执行 INSERT 查询，返回自增ID
            Number number = dataAccess.insertReturnId(sql, modifier, parameters);

            // 将结果转成方法的返回类型
            return new Identity(number);

        } else {

            // 执行 UPDATE / DELETE 查询
            int updated = dataAccess.update(sql, modifier, parameters);

            // 转换基本类型
            if (returnType.isPrimitive()) {
                returnType = ClassUtils.primitiveToWrapper(returnType);
            }

            // 将结果转成方法的返回类型
            if (returnType == Boolean.class) {
                return Boolean.valueOf(updated > 0);
            } else if (returnType == Long.class) {
                return Long.valueOf(updated);
            } else if (returnType == Integer.class) {
                return Integer.valueOf(updated);
            }
        }

        return null; // 没有返回值
    }
}
