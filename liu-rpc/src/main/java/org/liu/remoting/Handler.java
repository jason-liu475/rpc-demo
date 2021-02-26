package org.liu.remoting;

public interface Handler {
	void onReceive(LrpcChannel lrpcChannel,Object message) throws Exception;
	void onWrite(LrpcChannel lrpcChannel,Object message) throws Exception;
}
