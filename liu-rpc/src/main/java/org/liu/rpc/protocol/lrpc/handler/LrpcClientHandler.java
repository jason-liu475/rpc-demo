package org.liu.rpc.protocol.lrpc.handler;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.liu.remoting.Handler;
import org.liu.remoting.LrpcChannel;
import org.liu.rpc.Response;

public class LrpcClientHandler implements Handler {
	final static Map<Long, CompletableFuture> invokerMap = new ConcurrentHashMap<>();

	public static CompletableFuture waitResult(long messageId){
		CompletableFuture future = new CompletableFuture();
		invokerMap.put(messageId,future);
		return future;
	}

	@Override
	public void onReceive(LrpcChannel lrpcChannel, Object message) throws Exception {
		Response response = (Response)message;
		invokerMap.get(response.getRequestId()).complete(response);
		invokerMap.remove(response.getRequestId());
	}

	@Override
	public void onWrite(LrpcChannel lrpcChannel, Object message) throws Exception {

	}
}
