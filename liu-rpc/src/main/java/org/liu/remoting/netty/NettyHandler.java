package org.liu.remoting.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.liu.remoting.Handler;

@Slf4j
@AllArgsConstructor
public class NettyHandler extends ChannelDuplexHandler {
	private Handler handler;
	@Override
	@SneakyThrows
	public void channelRead(ChannelHandlerContext ctx, Object msg){
		log.info("内容:{}",msg);
		handler.onReceive(new NettyChannel(ctx.channel()),msg);
	}
}
