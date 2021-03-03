package org.liu.config;

import java.net.NetworkInterface;
import java.net.URI;

import lombok.extern.slf4j.Slf4j;
import org.liu.config.beans.ProtocolConfig;
import org.liu.config.beans.ServiceConfig;
import org.liu.rpc.Invoker;
import org.liu.rpc.protocol.Protocol;
import org.liu.rpc.proxy.ProxyFactory;
import org.liu.utils.tools.SpiUtils;

@Slf4j
public class LrpcBootstrap {

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
				stringBuilder.append(protocolConfig.getPort()).append("/");
				stringBuilder.append(serviceConfig.getServiceClass().getName()).append("?");
				stringBuilder.append("transporter=").append(protocolConfig.getTransporter()).append("&");
				stringBuilder.append("serialization=").append(protocolConfig.getSerialization());
				URI exportUri = new URI(stringBuilder.toString());
				log.info("准备暴露服务：{}",exportUri);
				Protocol protocol = SpiUtils.getServiceImpl(protocolConfig.getName(), Protocol.class);
				protocol.export(exportUri,invoker);
			}
		}catch (Throwable e) {
			log.error("暴露服务出现异常",e);
		}
	}
}
