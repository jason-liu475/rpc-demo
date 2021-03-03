package org.liu.rpc;

public interface Invoker {
	Class<?> getInterface();
	Object invoke(RpcInvocation rpcInvocation) throws Exception;
}
