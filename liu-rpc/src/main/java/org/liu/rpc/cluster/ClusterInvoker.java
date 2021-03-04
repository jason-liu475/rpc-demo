package org.liu.rpc.cluster;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import org.liu.config.beans.ReferenceConfig;
import org.liu.config.beans.RegistryConfig;
import org.liu.rpc.Invoker;
import org.liu.rpc.RpcInvocation;
import org.liu.rpc.protocol.Protocol;
import org.liu.rpc.registry.RegistryService;
import org.liu.utils.tools.SpiUtils;

/**
 * @author liu
 */
@Slf4j
public class ClusterInvoker implements Invoker {
	ReferenceConfig referenceConfig;
	LoadBalance loadBalance;
	final static Map<URI,Invoker> invokerMap = new ConcurrentHashMap<>();

	public ClusterInvoker(ReferenceConfig referenceConfig) throws Exception{
		this.referenceConfig = referenceConfig;
		this.loadBalance = SpiUtils.getServiceImpl(referenceConfig.getLoadBalance(), LoadBalance.class);
		String serviceName = referenceConfig.getServiceClass().getName();
		for (RegistryConfig registryConfig : referenceConfig.getRegistryConfigs()) {
			URI registryUri = new URI(registryConfig.getAddress());
			RegistryService registryService = SpiUtils.getServiceImpl(registryUri.getScheme(), RegistryService.class);
			registryService.init(registryUri);
			registryService.subscribe(serviceName, uris -> {
				log.info("更新前的服务invoker信息:{}",invokerMap);
				//剔除
				for (URI uri : invokerMap.keySet()) {
					if(!uris.contains(uri)){
						invokerMap.remove(uri);
					}
				}
				//新增
				for (URI uri : uris) {
					if(!invokerMap.containsKey(uri)) {
						Protocol protocol = SpiUtils.getServiceImpl(uri.getScheme(), Protocol.class);
						Invoker invoker = protocol.refer(uri);
						invokerMap.put(uri,invoker);
					}
				}
				log.info("更新后的服务invoker信息:{}",invokerMap);
			});
		}
	}
	@Override
	public Class<?> getInterface() {
		return this.referenceConfig.getServiceClass();
	}

	@Override
	public Object invoke(RpcInvocation rpcInvocation) throws Exception {
		Invoker select = this.loadBalance.select(invokerMap);
		return select.invoke(rpcInvocation);
	}
}
