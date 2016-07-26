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

/**
 * 在Jade DAO方法声明其返回类型为 {@link Identity}表示，在执行插入语句后返回数据库设置的ID（自增ID等）
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
public class Identity extends Number {

    /**
     * 生成的序列化 UID.
     */
    private static final long serialVersionUID = 6250174845871013763L;

    // 返回的对象  ID.
    protected Number number;

    /**
     * 构造对象容纳返回的对象 ID.
     * 
     * @param number - 返回的对象 ID
     */
    public Identity(Number number) {
        this.number = number;
    }

    @Override
    public int intValue() {
        return number.intValue();
    }

    @Override
    public long longValue() {
        return number.longValue();
    }

    @Override
    public float floatValue() {
        return number.floatValue();
    }

    @Override
    public double doubleValue() {
        return number.doubleValue();
    }

    @Override
    public String toString() {
        return Long.toString(number.longValue());
    }
}
