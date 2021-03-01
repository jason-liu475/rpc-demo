package protocol;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.liu.rpc.RpcInvocation;
import org.liu.utils.serialize.Serialization;
import org.liu.utils.tools.ByteUtil;
import org.liu.utils.tools.SpiUtils;
@Slf4j
public class ClientMock {
	public static void main(String[] args) throws Exception{
		RpcInvocation rpcInvocation = new RpcInvocation();
		rpcInvocation.setServiceName("org.liu.service.SmsService");
		rpcInvocation.setMethodName("sendMessage");
		rpcInvocation.setParameterTypes(new Class<?>[]{String.class});
		rpcInvocation.setArguments(new Object[]{"junit"});

		Serialization jsonSerialization = SpiUtils.getServiceImpl("JsonSerialization",Serialization.class);
		assert jsonSerialization != null;
		byte[] bytes = jsonSerialization.serialize(rpcInvocation);

		ByteBuf byteBuf = Unpooled.buffer();
		byteBuf.writeByte((byte) 0xda);
		byteBuf.writeByte((byte) 0xbb);
		byteBuf.writeBytes(ByteUtil.int2bytes(bytes.length));
		byteBuf.writeBytes(bytes);

		SocketChannel lrpcClient = SocketChannel.open();
		lrpcClient.connect(new InetSocketAddress("127.0.0.1",8081));
		lrpcClient.write(ByteBuffer.wrap(byteBuf.array()));

		ByteBuffer response = ByteBuffer.allocate(1025);
		lrpcClient.read(response);
		log.info("响应内容：{}",new String(response.array()));

	}
}
