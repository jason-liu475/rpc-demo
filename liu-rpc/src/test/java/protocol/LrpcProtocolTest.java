package protocol;

import java.net.URI;

import lombok.extern.slf4j.Slf4j;
import org.liu.remoting.netty.Netty4Transporter;
import org.liu.rpc.RpcInvocation;
import org.liu.rpc.protocol.lrpc.codec.LrpcCodec;
import org.liu.rpc.protocol.lrpc.handler.LrpcServerHandler;
import org.liu.utils.serialize.json.JsonSerialization;

@Slf4j
public class LrpcProtocolTest {
	public static void main(String[] args) throws Exception {
		LrpcCodec<RpcInvocation> lrpcCodec = new LrpcCodec<>();
		lrpcCodec.setSerialization(new JsonSerialization());
		lrpcCodec.setDecodeType(RpcInvocation.class);
		LrpcServerHandler lrpcServerHandler = new LrpcServerHandler();
		new Netty4Transporter().start(new URI("lrpc://127.0.0.1:8081"),
				lrpcCodec, lrpcServerHandler);
	}
}
