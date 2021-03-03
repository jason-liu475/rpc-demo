package org.liu.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Service;

@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface LiuRpcService {
	Class<?> interfaceClass() default void.class;
}
