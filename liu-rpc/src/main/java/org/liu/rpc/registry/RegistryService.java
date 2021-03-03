package org.liu.rpc.registry;

import java.net.URI;

public interface RegistryService {
	/**
	 * 注册
	 * @param uri
	 */
	void register(URI uri);

	/**
	 * 订阅
	 * @param service
	 * @param notifyListener
	 */
	void subscribe(String service,NotifyListener notifyListener);

	/**
	 * 配置连接信息
 	 * @param address
	 */
	void init(URI address);
}
