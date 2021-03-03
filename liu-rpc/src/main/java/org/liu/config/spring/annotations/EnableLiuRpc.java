package org.liu.config.spring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.liu.config.spring.LiuRpcConfiguration;
import org.liu.config.spring.LiuRpcPostProcessor;

import org.springframework.context.annotation.Import;

@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(value = {LiuRpcPostProcessor.class, LiuRpcConfiguration.class})
public @interface EnableLiuRpc {
	Class<?> interfaceClass() default void.class;
}
