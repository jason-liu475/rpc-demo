package org.liu.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.liu.config.LiuRpcPostProcessor;

import org.springframework.context.annotation.Import;

@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(value = {LiuRpcPostProcessor.class})
public @interface EnableLiuRpc {
	Class<?> interfaceClass() default void.class;
}
