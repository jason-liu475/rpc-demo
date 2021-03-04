package org.liu.remoting.netty;

import java.net.URI;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.liu.remoting.Client;
import org.liu.remoting.Codec;
import org.liu.remoting.Handler;
import org.liu.remoting.LrpcChannel;

@Slf4j
public class NettyClient implements Client {
	LrpcChannel channel = null;
	EventLoopGroup group = null;
	@Override
	public void connect(URI uri, Codec codec, Handler handler) {
		try {
			group = new NioEventLoopGroup();
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer() {
						@Override
						protected void initChannel(Channel ch) throws Exception {
							ch.pipeline().addLast(new NettyCodec(codec.newInstance()));
							ch.pipeline().addLast(new NettyHandler(handler));
						}
					});
			ChannelFuture channelFuture = bootstrap.connect(uri.getHost(), uri.getPort()).sync();
			channel = new NettyChannel(channelFuture.channel());
			//优雅停机
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					log.info("shutdown");
					synchronized (NettyServer.class){
						group.shutdownGracefully().sync();
					}
				}catch (Exception e){
					log.error("exception",e);
				}
			}));
		}catch (Exception e){
			log.error("exception",e);
		}
	}

	@Override
	public LrpcChannel getChannel() {
		return channel;
	}
}
