package org.liu.rpc.protocol;

import java.net.URI;

public interface Protocol {
	/**
	 *
	 * @param exportUri 协议名称://IP:端口/service全类名?参数1=xxx&参数2=xxx
	 */
	void export(URI exportUri);
}
