package org.liu.config;

import org.liu.utils.annotations.LiuRpcService;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class LiuRpcPostProcessor implements ApplicationContextAware, InstantiationAwareBeanPostProcessor {
	ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(bean.getClass().isAnnotationPresent(LiuRpcService.class)){
			System.out.println(bean.getClass().getSimpleName() + ":暴露服务，接受请求");
		}
		return bean;
	}
}
