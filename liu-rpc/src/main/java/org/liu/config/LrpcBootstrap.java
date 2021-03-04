package org.liu.config;

import java.net.NetworkInterface;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.liu.config.beans.ProtocolConfig;
import org.liu.config.beans.ReferenceConfig;
import org.liu.config.beans.RegistryConfig;
import org.liu.config.beans.ServiceConfig;
import org.liu.rpc.Invoker;
import org.liu.rpc.cluster.ClusterInvoker;
import org.liu.rpc.protocol.Protocol;
import org.liu.rpc.proxy.ProxyFactory;
import org.liu.rpc.registry.RegistryService;
import org.liu.utils.tools.SpiUtils;

@Slf4j
public class LrpcBootstrap {
	private static final Set<String> serverPortSet = new HashSet<>();
	public static void export(ServiceConfig serviceConfig){
		Invoker invoker = ProxyFactory.getInvoker(serviceConfig.getReference(), serviceConfig.getServiceClass());
		try {
			for (ProtocolConfig protocolConfig : serviceConfig.getProtocolConfigs()) {
				// 协议名称://IP:端口/service全类名?参数1=xxx&参数2=xxx
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(protocolConfig.getName()).append("://");
				String hostAddress = NetworkInterface.getNetworkInterfaces().nextElement()
							.getInterfaceAddresses().get(0).getAddress().getHostAddress();

				stringBuilder.append(hostAddress).append(":");
				stringBuilder.append(protocolConfig.getPort());
				String server = stringBuilder.toString();
				stringBuilder.append("/").append(serviceConfig.getServiceClass().getName()).append("?");
				stringBuilder.append("transporter=").append(protocolConfig.getTransporter()).append("&");
				stringBuilder.append("serialization=").append(protocolConfig.getSerialization());
				URI exportUri = new URI(stringBuilder.toString());
				log.info("准备暴露服务：{}",exportUri);
				if(!serverPortSet.contains(server)){
					Protocol protocol = SpiUtils.getServiceImpl(protocolConfig.getName(), Protocol.class);
					protocol.export(exportUri,invoker);
					serverPortSet.add(server);
				}
				for (RegistryConfig registryConfig : serviceConfig.getRegistryConfigs()) {
					URI uri = new URI(registryConfig.getAddress());
					RegistryService register = SpiUtils.getServiceImpl(uri.getScheme(), RegistryService.class);
					register.init(uri);
					register.register(exportUri);
				}

			}
		}catch (Throwable e) {
			log.error("暴露服务出现异常",e);
		}
	}
	public static Object getReferenceBean(ReferenceConfig referenceConfig){
		try {
			ClusterInvoker clusterInvoker = new ClusterInvoker(referenceConfig);
//			LrpcProtocol lrpcProtocol = new LrpcProtocol();
//			Invoker invoker = lrpcProtocol.refer(new URI("LrpcProtocol://127.0.0.1:8081/org.liu.service.SmsService?transporter=Netty4Transporter&serialization=JsonSerialization"));
			Object proxy = ProxyFactory.getProxy(clusterInvoker, new Class[]{referenceConfig.getServiceClass()});
			return proxy;
		}catch (Exception e){
			log.error("创建代理对象失败",e);
		}
		return null;
	}
}
