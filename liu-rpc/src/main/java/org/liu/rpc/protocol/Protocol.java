package org.liu.rpc.protocol;

import java.net.URI;

import org.liu.rpc.Invoker;

public interface Protocol {
	/**
	 *
	 * @param exportUri 协议名称://IP:端口/service全类名?参数1=xxx&参数2=xxx
	 * @param invoker 调用具体实现类的对象
	 */
	void export(URI exportUri, Invoker invoker);
}
