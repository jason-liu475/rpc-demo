package org.liu.remoting.netty;

import java.net.URI;

import org.liu.remoting.Server;
import org.liu.remoting.Transporter;

public class Netty4Transporter implements Transporter {
	@Override
	public Server start(URI uri) {
		NettyServer server = new NettyServer();
		server.start(uri);
		return server;
	}
}
