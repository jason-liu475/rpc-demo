package protocol;

import java.net.URI;

import lombok.extern.slf4j.Slf4j;
import org.liu.remoting.netty.Netty4Transporter;
import org.liu.rpc.RpcInvocation;
import org.liu.rpc.protocol.lrpc.codec.LrpcCodec;
import org.liu.rpc.protocol.lrpc.handler.LrpcServerHandler;

@Slf4j
public class RemotingTest {
	public static void main(String[] args) throws Exception {
		new Netty4Transporter().start(new URI("lrpc://127.0.0.1:8081"),
				new LrpcCodec<RpcInvocation>(), new LrpcServerHandler());
	}
}
