package org.liu.config;

import java.net.URI;
import java.util.Objects;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.liu.bean.ProtocolConfig;
import org.liu.bean.RegistryConfig;
import org.liu.remoting.Transporter;
import org.liu.utils.annotations.LiuRpcService;
import org.liu.utils.tools.SpiUtils;

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
			ProtocolConfig protocolConfig = applicationContext.getBean(ProtocolConfig.class);
			Transporter transporter = SpiUtils.getServiceImpl(protocolConfig.getTransporter(), Transporter.class);
			assert transporter != null;
			transporter.start(new URI(protocolConfig.getName() + "://127.0.0.1:" + protocolConfig.getPort()));
		}
		if(Objects.equals(ProtocolConfig.class,bean.getClass())){
			ProtocolConfig protocolConfig = (ProtocolConfig)bean;
			log.info("ProtocolConfig配置文件加载成功 name:{}",protocolConfig.getName());
		}
		if(Objects.equals(RegistryConfig.class,bean.getClass())){
			RegistryConfig registryConfig = (RegistryConfig)bean;
			log.info("RegistryConfig配置文件加载成功 address:{}",registryConfig.getAddress());
		}
		return bean;
	}
}
