package org.liu.remoting;

public interface LrpcChannel {
	void send(byte[] message);
}
