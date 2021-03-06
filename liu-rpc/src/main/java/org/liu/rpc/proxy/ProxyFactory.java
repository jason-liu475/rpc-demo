package org.liu.rpc.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.liu.rpc.Invoker;
import org.liu.rpc.RpcInvocation;

public class ProxyFactory {
	public static Invoker getInvoker(Object proxy,Class<?> type){
		return new Invoker() {
			@Override
			public Class<?> getInterface() {
				return type;
			}

			@Override
			public Object invoke(RpcInvocation rpcInvocation) throws Exception {
				//反射调用方法
				Method method = proxy.getClass().getMethod(rpcInvocation.getMethodName(), rpcInvocation.getParameterTypes());
				return method.invoke(proxy,rpcInvocation.getArguments());
			}
		};
	}
	public static Object getProxy(Invoker invoker,Class<?>[] interfaces){
		return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, new InvokerInvocationHandler(invoker));
	}
}
