package org.liu.rpc.cluster.loadbalance;

import java.net.URI;
import java.util.Map;
import java.util.Random;

import org.liu.rpc.Invoker;
import org.liu.rpc.cluster.LoadBalance;

/**
 * @author liu
 */
public class RandomLoadBalance implements LoadBalance {
	@Override
	public Invoker select(Map<URI, Invoker> invokerMap) {
		int index = new Random().nextInt(invokerMap.size());
		return invokerMap.values().toArray(new Invoker[]{})[index];
	}
}
