package org.liu.rpc.protocol.lrpc.codec;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.liu.remoting.Codec;
import org.liu.utils.serialize.Serialization;
import org.liu.utils.tools.ByteUtil;

/**
 * lrpc: MAGIC + msgLength + msgContent
 * MAGIC: 2字节
 * msgLength: 4字节
 * (MAGIC + msgLength)为协议头部
 *
 */
@Data
@Slf4j
public class LrpcCodec<T>implements Codec<T> {

	public final static byte[] MAGIC = new byte[]{(byte) 0xda, (byte) 0xbb};

	public final static int HEADER_LEN = 6;
	private Serialization serialization;
	private Class<T> decodeType;
	//临时存储数据 解决拆包问题:接收到一部分数据包 临时存储 跟下次的数据合并
	ByteBuf tempMsg = Unpooled.buffer();

	@Override
	public byte[] encode(Object msg) throws Exception {
		return new byte[0];
	}

	@Override
	public List<T> decode(byte[] data) throws Exception {
		//解决粘包问题 TCP连接发送多个包 根据协议把包拆分成多个RpcInvocation对象
		List<T> out = new ArrayList<>();
		//1.解析(解析头部，取出数据，封装成RpcInvocation)
		ByteBuf message = Unpooled.buffer();
		int tmpMsgSize = tempMsg.readableBytes();
		//如果暂存有上一次余下的请求报文，则合并
		if(tmpMsgSize > 0){
			message.writeBytes(tempMsg);
			message.writeBytes(data);
			log.debug("合并：上一次数据报文余下长度为：{}",tmpMsgSize);
		}else{
			message.writeBytes(data);
		}
		//解决粘包问题
		while(true) {
			if (HEADER_LEN >= message.readableBytes()) {
				//接收到的报文小于协议头部长度 先缓存起来 跟下次报文拼起来
				tempMsg.clear();
				tempMsg.writeBytes(message);
				return out;
			}

			//检查关键字
			byte[] magic = new byte[2];
			message.readBytes(magic);
			while (true) {
				if (magic[0] != MAGIC[0] || magic[1] != MAGIC[1]) {
					if (message.readableBytes() == 0) {
						//所有数据都读完了没发现正确的协议头 丢弃本次数据 等下次数据
						tempMsg.clear();
						tempMsg.writeByte(magic[1]);
						return out;
					}
					magic[0] = magic[1];
					magic[1] = message.readByte();
				}
				else {
					break;
				}
			}
			byte[] lengthBytes = new byte[4];
			message.readBytes(lengthBytes);
			int length = ByteUtil.Bytes2Int_BE(lengthBytes);
			//读取body 如果body没传输完 先不处理
			if(message.readableBytes() < length){
				tempMsg.clear();
				tempMsg.writeBytes(magic);
				tempMsg.writeBytes(lengthBytes);
				tempMsg.writeBytes(message);
				return out;
			}
			byte[] body = new byte[length];
			message.readBytes(body);
			T rpcInvocation = getSerialization().deserialize(body, decodeType);
			out.add(rpcInvocation);
		}
	}

	@Override
	public Codec<T> newInstance() {
		LrpcCodec<T> lrpcCodec = new LrpcCodec<>();
		lrpcCodec.setSerialization(this.getSerialization());
		lrpcCodec.setDecodeType(this.getDecodeType());
		return lrpcCodec;
	}
}
