package org.liu.config.spring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Service;

@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface LiuRpcReference {
	String loadBalance() default "RandomLoadBalance";
}
