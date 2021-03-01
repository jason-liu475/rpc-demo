package org.liu.remoting.netty;

import java.net.InetSocketAddress;
import java.net.URI;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.liu.remoting.Codec;
import org.liu.remoting.Handler;
import org.liu.remoting.Server;

@Slf4j
public class NettyServer implements Server {
	EventLoopGroup boss = new NioEventLoopGroup();
	EventLoopGroup worker = new NioEventLoopGroup();
	@Override
	public void start(URI uri, Codec codec, Handler handler) {
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(boss, worker)
					.channel(NioServerSocketChannel.class)
					.localAddress(new InetSocketAddress(uri.getHost(), uri.getPort()))
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							socketChannel.pipeline().addLast(new NettyCodec(codec.newInstance()));
							socketChannel.pipeline().addLast(new NettyHandler(handler));
						}
					});
			ChannelFuture future = serverBootstrap.bind().sync();
			log.info("完成端口绑定和服务器启动");
		}catch (Exception e){
			log.error(e.getMessage());
		}
	}
}
