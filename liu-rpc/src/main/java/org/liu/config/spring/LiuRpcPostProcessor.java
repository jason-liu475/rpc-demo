package org.liu.config.spring;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.liu.config.LrpcBootstrap;
import org.liu.config.beans.ProtocolConfig;
import org.liu.config.beans.ReferenceConfig;
import org.liu.config.beans.RegistryConfig;
import org.liu.config.beans.ServiceConfig;
import org.liu.config.spring.annotations.LiuRpcReference;
import org.liu.config.spring.annotations.LiuRpcService;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Slf4j
public class LiuRpcPostProcessor implements ApplicationContextAware, InstantiationAwareBeanPostProcessor {
	Map<Class<?>,Map<String,Object>> proxyMap = new ConcurrentHashMap<>(256);
	ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	@SneakyThrows
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		//服务端暴露服务
		if(bean.getClass().isAnnotationPresent(LiuRpcService.class)){
			log.info("{}:暴露服务，接受请求",bean.getClass().getSimpleName());
			ServiceConfig serviceConfig = new ServiceConfig();
			serviceConfig.addProtocolConfig(applicationContext.getBean(ProtocolConfig.class));
			serviceConfig.addRegistryConfig(applicationContext.getBean(RegistryConfig.class));
			serviceConfig.setReference(bean);
			LiuRpcService annotation = bean.getClass().getAnnotation(LiuRpcService.class);
			if(annotation.interfaceClass() == void.class){
				serviceConfig.setServiceClass(bean.getClass().getInterfaces()[0]);
			}else{
				serviceConfig.setServiceClass(annotation.interfaceClass());
			}

			LrpcBootstrap.export(serviceConfig);
		}
		//客户端注入远程代理bean
		for (Field field : bean.getClass().getDeclaredFields()) {
			if(!field.isAnnotationPresent(LiuRpcReference.class)){
				continue;
			}
			Object referenceBean;
			String loadBalance = field.getAnnotation(LiuRpcReference.class).loadBalance();
			if(!proxyMap.containsKey(field.getType())) {
				ReferenceConfig referenceConfig = new ReferenceConfig();
				referenceConfig.addProtocolConfig(applicationContext.getBean(ProtocolConfig.class));
				referenceConfig.addRegistryConfig(applicationContext.getBean(RegistryConfig.class));
				referenceConfig.setServiceClass(field.getType());
				referenceConfig.setLoadBalance(loadBalance);
				referenceBean = LrpcBootstrap.getReferenceBean(referenceConfig);
				Map<String,Object> map = new HashMap<>();
				map.put(referenceConfig.getLoadBalance(),referenceBean);
				proxyMap.put(field.getType(),map);
			}else{
				Map<String,Object> map = proxyMap.get(field.getType());
				if(map.containsKey(loadBalance)){
					referenceBean = map.get(loadBalance);
				}else{
					ReferenceConfig referenceConfig = new ReferenceConfig();
					referenceConfig.addProtocolConfig(applicationContext.getBean(ProtocolConfig.class));
					referenceConfig.addRegistryConfig(applicationContext.getBean(RegistryConfig.class));
					referenceConfig.setServiceClass(field.getType());
					referenceConfig.setLoadBalance(loadBalance);
					referenceBean = LrpcBootstrap.getReferenceBean(referenceConfig);
					map.put(referenceConfig.getLoadBalance(),referenceBean);
				}
			}
			field.setAccessible(true);
			field.set(bean,referenceBean);
		}
		if(Objects.equals(RegistryConfig.class,bean.getClass())){
			RegistryConfig registryConfig = (RegistryConfig)bean;
			log.info("RegistryConfig配置文件加载成功 address:{}",registryConfig.getAddress());
		}
		if(Objects.equals(ProtocolConfig.class,bean.getClass())){
			ProtocolConfig protocolConfig = (ProtocolConfig)bean;
			log.info("ProtocolConfig配置文件加载成功 name:{}",protocolConfig.getName());
		}
		return bean;
	}
}
