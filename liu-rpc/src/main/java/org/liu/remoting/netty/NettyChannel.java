package org.liu.remoting.netty;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.liu.remoting.LrpcChannel;

@NoArgsConstructor
@AllArgsConstructor
public class NettyChannel implements LrpcChannel {
	private Channel channel;
	@Override
	public void send(byte[] message) {
		channel.writeAndFlush(message);
	}
}
