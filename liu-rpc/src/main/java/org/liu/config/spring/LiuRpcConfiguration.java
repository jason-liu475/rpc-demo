package org.liu.config.spring;

import java.lang.reflect.Field;

import lombok.Data;
import org.liu.config.beans.ProtocolConfig;
import org.liu.config.beans.RegistryConfig;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;

@Data
public class LiuRpcConfiguration implements ImportBeanDefinitionRegistrar {

	private StandardEnvironment environment;

	public LiuRpcConfiguration(Environment environment){
		this.environment = (StandardEnvironment)environment;
	}


	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
			BeanNameGenerator importBeanNameGenerator) {
		BeanDefinitionBuilder beanDefinitionBuilder = null;
		beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ProtocolConfig.class);
		for (Field field : ProtocolConfig.class.getDeclaredFields()) {
			String value = environment.getProperty("lrpc.protocol." + field.getName());
			beanDefinitionBuilder.addPropertyValue(field.getName(),value);
		}
		registry.registerBeanDefinition("protocolConfig",beanDefinitionBuilder.getBeanDefinition());

		beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RegistryConfig.class);
		for (Field field : RegistryConfig.class.getDeclaredFields()) {
			String value = environment.getProperty("lrpc.registry." + field.getName());
			beanDefinitionBuilder.addPropertyValue(field.getName(),value);
		}
		registry.registerBeanDefinition("registryConfig",beanDefinitionBuilder.getBeanDefinition());

	}
}
