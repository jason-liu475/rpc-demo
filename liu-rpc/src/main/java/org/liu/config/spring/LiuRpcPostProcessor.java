package org.liu.config.spring;

import java.util.Objects;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.liu.config.LrpcBootstrap;
import org.liu.config.annotations.LiuRpcService;
import org.liu.config.beans.ProtocolConfig;
import org.liu.config.beans.RegistryConfig;
import org.liu.config.beans.ServiceConfig;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Slf4j
public class LiuRpcPostProcessor implements ApplicationContextAware, InstantiationAwareBeanPostProcessor {
	ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	@SneakyThrows
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
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
			//transporter.start(new URI(protocolConfig.getName() + "://127.0.0.1:" + protocolConfig.getPort()));
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
