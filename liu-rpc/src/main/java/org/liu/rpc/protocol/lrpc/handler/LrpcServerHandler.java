package org.liu.rpc.protocol.lrpc.handler;

import lombok.extern.slf4j.Slf4j;
import org.liu.remoting.Handler;
import org.liu.remoting.LrpcChannel;
import org.liu.rpc.RpcInvocation;
@Slf4j
public class LrpcServerHandler implements Handler {
	@Override
	public void onReceive(LrpcChannel lrpcChannel, Object message) throws Exception {
		RpcInvocation rpcInvocation = (RpcInvocation) message;
		log.info("收到rpcInvocation信息：{}",rpcInvocation.toString());
	}

	@Override
	public void onWrite(LrpcChannel lrpcChannel, Object message) throws Exception {

	}
}
