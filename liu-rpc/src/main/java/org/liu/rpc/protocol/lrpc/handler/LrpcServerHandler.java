package org.liu.rpc.protocol.lrpc.handler;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.liu.remoting.Handler;
import org.liu.remoting.LrpcChannel;
import org.liu.rpc.Invoker;
import org.liu.rpc.Response;
import org.liu.rpc.RpcInvocation;
import org.liu.utils.serialize.Serialization;

@Data
@Slf4j
public class LrpcServerHandler implements Handler {
	private Invoker invoker;
	private Serialization serialization;
	@Override
	public void onReceive(LrpcChannel lrpcChannel, Object message) throws Exception {
		RpcInvocation rpcInvocation = (RpcInvocation) message;
		log.info("收到rpcInvocation信息：{}",rpcInvocation.toString());
		Response response = new Response();
		try {
			Object result = this.invoker.invoke(rpcInvocation);
			log.debug("服务端执行结果：{}",result.toString());
			response.setStatus(200);
			response.setContent(result);
			response.setRequestId(rpcInvocation.getId());
		}catch (Throwable e){
			response.setStatus(99);
			response.setContent(e.getMessage());
			log.error("调用服务异常",e);
		}
		byte[] responseBody = this.serialization.serialize(response);
		lrpcChannel.send(responseBody);
	}

	@Override
	public void onWrite(LrpcChannel lrpcChannel, Object message) throws Exception {

	}
}
