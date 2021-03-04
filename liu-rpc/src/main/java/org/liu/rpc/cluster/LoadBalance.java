package org.liu.rpc.cluster;

import java.net.URI;
import java.util.Map;

import org.liu.rpc.Invoker;

public interface LoadBalance {
	Invoker select(Map<URI,Invoker> invokerMap);
}
