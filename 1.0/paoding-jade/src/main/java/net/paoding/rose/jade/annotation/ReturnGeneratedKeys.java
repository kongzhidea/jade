package net.paoding.rose.jade.annotation;

import java.lang.annotation.*;

/**
 * 返回自增Id， 代替Identity
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReturnGeneratedKeys {
}
