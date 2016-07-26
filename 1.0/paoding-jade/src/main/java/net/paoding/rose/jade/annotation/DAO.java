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
package net.paoding.rose.jade.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 用此{@link DAO}注解标注在一个符合Jade编写规范的DAO接口类上，明确标注这是Jade DAO接口。
 * 
 * <p>
 * 只要符合2个原则的类将被识别为Jade DAO：<br>
 * <ul>
 * <li>DAO必须是独立的接口类，即：不能是内部类接口；</li>
 * <li>DAO接口上标注了DAO类，或所继承的父接口标注了此注解(即{@link DAO}注解)；</li>
 * </ul>
 * <p>
 * 
 * properties文件中包含rose=dao的标识( 标识rose=*亦可以，但建议开发者们精确标志dao而非*，这有助于提高启动速度)。
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DAO {

    String catalog() default "";
}
