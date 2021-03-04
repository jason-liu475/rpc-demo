package org.liu.remoting.netty;

import java.net.URI;

import org.liu.remoting.Client;
import org.liu.remoting.Codec;
import org.liu.remoting.Handler;
import org.liu.remoting.Server;
import org.liu.remoting.Transporter;

public class Netty4Transporter implements Transporter {
	@Override
	public Server start(URI uri,Codec codec, Handler handler) {
		NettyServer server = new NettyServer();
		server.start(uri, codec, handler);
		return server;
	}

	@Override
	public Client connect(URI uri, Codec codec, Handler handler) {
		NettyClient nettyClient = new NettyClient();
		nettyClient.connect(uri,codec,handler);
		return nettyClient;
	}
}
