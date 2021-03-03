package org.liu.remoting.netty;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.liu.remoting.Codec;

/**
 * 接收端：把网络请求中的字节数组转成RpcInvocation
 * 发送端：发送的数据以特定的协议格式进行发送
 * @author liu
 */
@Slf4j
@AllArgsConstructor
public class NettyCodec extends ChannelDuplexHandler {
	private Codec codec;
	@Override
	@SneakyThrows
	public void channelRead(ChannelHandlerContext ctx, Object msg){
		//编解码
		//1. 读取数据
		ByteBuf data = (ByteBuf)msg;
		byte[] bytes = new byte[data.readableBytes()];
		data.readBytes(bytes);
		// 2. 格式转换
		List<Object> out = codec.decode(bytes);
		// 3. 处理器继续处理 - 决定下一个处理器 处理数据的次数
		for (Object o : out) {
			//责任链模式
			ctx.fireChannelRead(o);
		}
		log.info("内容:{}",msg);
	}
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		byte[] encode = codec.encode(msg);
		super.write(ctx, Unpooled.wrappedBuffer(encode),promise);
	}
}
