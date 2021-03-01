package org.liu.rpc.protocol.lrpc;

import java.net.URI;

import org.liu.remoting.Handler;
import org.liu.remoting.LrpcChannel;
import org.liu.remoting.Transporter;
import org.liu.rpc.protocol.Protocol;
import org.liu.rpc.protocol.lrpc.codec.LrpcCodec;
import org.liu.utils.tools.SpiUtils;
import org.liu.utils.tools.URIUtils;

public class LrpcProtocol implements Protocol {
	@Override
	public void export(URI exportUri) {
		String transporterName = URIUtils.getParam(exportUri, "transporter");
		Transporter transporter = SpiUtils.getServiceImpl(transporterName, Transporter.class);
		transporter.start(exportUri, new LrpcCodec(), new Handler() {
			@Override
			public void onReceive(LrpcChannel lrpcChannel, Object message) throws Exception {

			}

			@Override
			public void onWrite(LrpcChannel lrpcChannel, Object message) throws Exception {

			}
		});

	}
}
