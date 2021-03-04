package org.liu.rpc.protocol.lrpc;

import java.net.URI;

import org.liu.remoting.Client;
import org.liu.remoting.Transporter;
import org.liu.rpc.Invoker;
import org.liu.rpc.Response;
import org.liu.rpc.RpcInvocation;
import org.liu.rpc.protocol.Protocol;
import org.liu.rpc.protocol.lrpc.codec.LrpcCodec;
import org.liu.rpc.protocol.lrpc.handler.LrpcClientHandler;
import org.liu.rpc.protocol.lrpc.handler.LrpcServerHandler;
import org.liu.utils.serialize.Serialization;
import org.liu.utils.tools.SpiUtils;
import org.liu.utils.tools.URIUtils;

public class LrpcProtocol implements Protocol {
	@Override
	public void export(URI exportUri, Invoker invoker) {
		String serializationName = URIUtils.getParam(exportUri, "serialization");
		Serialization serialization = SpiUtils.getServiceImpl(serializationName, Serialization.class);
		LrpcCodec<RpcInvocation> lrpcCodec = new LrpcCodec<>();
		lrpcCodec.setSerialization(serialization);
		lrpcCodec.setDecodeType(RpcInvocation.class);
		LrpcServerHandler lrpcServerHandler = new LrpcServerHandler();
		lrpcServerHandler.setInvoker(invoker);
		lrpcServerHandler.setSerialization(serialization);
		String transporterName = URIUtils.getParam(exportUri, "transporter");
		Transporter transporter = SpiUtils.getServiceImpl(transporterName, Transporter.class);
		transporter.start(exportUri, lrpcCodec, lrpcServerHandler);
	}

	@Override
	public Invoker refer(URI consumerUri) {
		//找到序列化
		String serializationName = URIUtils.getParam(consumerUri, "serialization");
		Serialization serialization = SpiUtils.getServiceImpl(serializationName, Serialization.class);
		//编解码器
		LrpcCodec codec = new LrpcCodec();
		codec.setDecodeType(Response.class);
		codec.setSerialization(serialization);
		//收到响应 处理
		LrpcClientHandler lrpcClientHandler = new LrpcClientHandler();

		//连接--长连接
		String transporterName = URIUtils.getParam(consumerUri, "transporter");
		Transporter transporter = SpiUtils.getServiceImpl(transporterName, Transporter.class);
		Client client = transporter.connect(consumerUri, codec, lrpcClientHandler);
		LrpcClientInvoker lrpcClientInvoker = new LrpcClientInvoker(client,serialization);
		return lrpcClientInvoker;
	}
}
