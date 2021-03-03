package org.liu.rpc.protocol.lrpc;

import java.net.URI;

import org.liu.remoting.Transporter;
import org.liu.rpc.Invoker;
import org.liu.rpc.RpcInvocation;
import org.liu.rpc.protocol.Protocol;
import org.liu.rpc.protocol.lrpc.codec.LrpcCodec;
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
}
