package remoting;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.liu.remoting.Codec;
import org.liu.remoting.Handler;
import org.liu.remoting.LrpcChannel;
import org.liu.remoting.netty.Netty4Transporter;

@Slf4j
public class RemotingTest {
	public static void main(String[] args) throws Exception {
		new Netty4Transporter().start(new URI("lrpc://127.0.0.1:8081"),new Codec(){
			@Override
			public byte[] encode(Object msg) throws Exception {
				return new byte[0];
			}

			@Override
			public List<Object> decode(byte[] bytes) throws Exception {
				List<Object> res = new ArrayList<>();
				log.info("解码:{}",new String(bytes));
				res.add("1" + new String(bytes));
				res.add("2" + new String(bytes));
				res.add("3" + new String(bytes));
				return res;
			}
		}, new Handler(){
			@Override
			public void onReceive(LrpcChannel lrpcChannel, Object message) throws Exception {
				log.info("onReceive:{}",message);
			}

			@Override
			public void onWrite(LrpcChannel lrpcChannel, Object message) throws Exception {

			}
		});
	}
}
