package org.liu.rpc.protocol.lrpc;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.liu.remoting.Client;
import org.liu.rpc.Invoker;
import org.liu.rpc.Response;
import org.liu.rpc.RpcInvocation;
import org.liu.rpc.protocol.lrpc.handler.LrpcClientHandler;
import org.liu.utils.serialize.Serialization;
@Slf4j
@AllArgsConstructor
public class LrpcClientInvoker implements Invoker {
	Client client;
	Serialization serialization;
	@Override
	public Class<?> getInterface() {
		return null;
	}

	@Override
	public Object invoke(RpcInvocation rpcInvocation) throws Exception {
		//序列化
		byte[] bytes = serialization.serialize(rpcInvocation);

		//发起请求
		this.client.getChannel().send(bytes);

		//等待结果
		CompletableFuture future = LrpcClientHandler.waitResult(rpcInvocation.getId());
		Object result = future.get(60, TimeUnit.SECONDS);
		Response response = (Response)result;
		if(response.getStatus() == 200){
			return response.getContent();
		}else{
			throw new Exception("server error:"+response.getContent().toString());
		}
	}
}
