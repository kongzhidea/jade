package net.paoding.rose.jade.provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.paoding.rose.jade.annotation.ShardParam;
import net.paoding.rose.jade.core.GenericUtils;

/**
 * 提供 Modifier 包装对 DAO 方法的访问。
 *
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
public class Modifier {

    private final Definition definition; // 对Dao的封装

    private final Method method; // dao里面的方法

    private final Class<?>[] genericReturnTypes;// 返回值类型，list map里面的泛型

    // key：参数注解， value： 在method中 此注解对应的 列表
    private final Map<Class<? extends Annotation>, Annotation[]> parameterAnnotations = new HashMap<Class<? extends Annotation>, Annotation[]>(
            8, 1.0f);

    private final int parameterCount; // 方法参数数量

    /**
     * <code>@{@link ShardParam}</code>标注在哪个参数上？(从0开始，负数代表无)－从method中获取并缓存
     */
    private int shardParamIndex;

    private ShardParam shardParam;

    public Modifier(Definition definition, Method method) {
        this.definition = definition;
        this.method = method;

        genericReturnTypes = GenericUtils.resolveTypeParameters(method.getDeclaringClass(), method.getGenericReturnType());

        // 获取对应参数， 以及参数对应的 注解
        Annotation[][] annotations = method.getParameterAnnotations();
        parameterCount = annotations.length;

        for (int index = 0; index < annotations.length; index++) {
            for (Annotation annotation : annotations[index]) {

                Class<? extends Annotation> annotationType = annotation.annotationType();
                Annotation[] annotationArray = parameterAnnotations.get(annotationType);
                if (annotationArray == null) {
                    annotationArray = (Annotation[]) Array.newInstance( // NL
                            annotationType, parameterCount);
                    parameterAnnotations.put(annotationType, annotationArray);
                }

                annotationArray[index] = annotation;
            }
        }

        int shardByIndex = -1;
        ShardParam shardBy = null;
        for (int index = 0; index < annotations.length; index++) {
            for (Annotation annotation : annotations[index]) {
                if (annotation instanceof ShardParam) {
                    if (shardByIndex >= 0) {
                        throw new IllegalArgumentException(
                                "duplicated @" + ShardParam.class.getName());
                    }
                    shardByIndex = index;
                    shardBy = (ShardParam) annotation;
                }
            }

        }
        this.shardParamIndex = shardByIndex;
        this.shardParam = shardBy;
    }

    public String getName() {
        return method.getName();
    }

    public Definition getDefinition() {
        return definition;
    }

    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    public Class<?>[] getGenericReturnTypes() {
        return genericReturnTypes;
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

    public Method getMethod() {
        return method;
    }

    // 获取方法中 某注解里
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T[] getParameterAnnotations(Class<T> annotationClass) {
        T[] annotations = (T[]) parameterAnnotations.get(annotationClass);
        if (annotations == null) {
            annotations = (T[]) Array.newInstance(annotationClass, parameterCount);
            parameterAnnotations.put(annotationClass, annotations);
        }
        return annotations;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Modifier) {
            Modifier modifier = (Modifier) obj;
            return definition.equals(modifier.definition) && method.equals(modifier.method);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return definition.hashCode() ^ method.hashCode();
    }

    @Override
    public String toString() {
        return definition.getName() + '#' + method.getName();
    }

    public int getShardParamIndex() {
        return shardParamIndex;
    }

    public ShardParam getShardParam() {
        return shardParam;
    }
}
