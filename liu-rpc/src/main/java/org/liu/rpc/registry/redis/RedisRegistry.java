package org.liu.rpc.registry.redis;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.liu.rpc.registry.NotifyListener;
import org.liu.rpc.registry.RegistryService;
import org.liu.utils.tools.URIUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * @author liu
 */
@Data
@Slf4j
public class RedisRegistry implements RegistryService {
	//15秒过期
	private static final int TIME_OUT = 15;
	private static final String KEY_PREFIX = "lrpc:";
	//注册中心地址
	private URI address;
	List<URI> servicesHeartBeat = new ArrayList<>();
	private JedisPubSub jedisPubSub;
	Map<String, Set<URI>> localCache = new ConcurrentHashMap<>();
	Map<String,NotifyListener> listenerMap = new ConcurrentHashMap<>();

	private static final AtomicInteger poolNumber = new AtomicInteger(1);
	ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(5, r -> {
		Thread thread = new Thread(r);
		thread.setName("register-thread-" + poolNumber.getAndIncrement());
		return thread;
	});
	@Override
	public void register(URI uri) {
		String key = KEY_PREFIX + uri.toString();
		log.debug("注册服务到redis,key:{}",key);
		Jedis jedis = new Jedis(address.getHost(),address.getPort());
		jedis.setex(key,TIME_OUT,String.valueOf(System.currentTimeMillis()));
		jedis.close();
		servicesHeartBeat.add(uri);
	}

	@Override
	public void subscribe(String service, NotifyListener notifyListener) {
		try {
			if(localCache.get(service) == null){
				localCache.putIfAbsent(service,new HashSet<>());
				listenerMap.put(service,notifyListener);
				Jedis jedis = new Jedis(address.getHost(),address.getPort());
				//String key = KEY_PREFIX + service;
				Set<String> serviceInstances = jedis.keys(KEY_PREFIX + "*" + service + "*");
				for (String serviceInstance : serviceInstances) {
					URI instanceUri = new URI(serviceInstance.replace(KEY_PREFIX,""));
					localCache.get(service).add(instanceUri);
				}
				notifyListener.notify(localCache.get(service));
			}
		}catch (Exception e){
			log.error("exception",e);
		}
	}

	@Override
	public void init(URI address) {
		this.address = address;
		executorService.scheduleWithFixedDelay(()->{
			Jedis jedis = new Jedis(address.getHost(),address.getPort());
			for (URI service : servicesHeartBeat) {
				String key = KEY_PREFIX + service.toString();
				log.debug("服务续订HeartBeat,key:{}",key);
				//续订
				jedis.expire(key,TIME_OUT);
			}
			jedis.close();
		},3000,5000, TimeUnit.MILLISECONDS);
		// 监听服务变动 - redis.conf  notify-keyspace-events KE$xg
		executorService.execute(() -> {
			try {
				jedisPubSub = new JedisPubSub() {
					@Override
					public void onPSubscribe(String pattern, int subscribedChannels) {
						log.info("注册中心开始监听:" + pattern);
					}

					@Override
					public void onPMessage(String pattern, String channel, String message) {
						try {
							URI serviceURI = new URI(channel.replace("__keyspace@0__:" + KEY_PREFIX, ""));
							if ("set".equals(message)) {
								// 新增
								Set<URI> uris = localCache.get(URIUtils.getService(serviceURI));
								if (uris != null) {
									uris.add(serviceURI);
								}
							}
							if ("expired".equals(message)) {
								// 过期
								Set<URI> uris = localCache.get(URIUtils.getService(serviceURI));
								if (uris != null) {
									uris.remove(serviceURI);
								}
							}
							if ("set".equals(message) || "expired".equals(message)) {
								log.info("服务实例有变化，开始刷新");
								NotifyListener notifyListener = listenerMap.get(URIUtils.getService(serviceURI));
								if (notifyListener != null) {
									notifyListener.notify(localCache.get(URIUtils.getService(serviceURI)));
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				Jedis jedis = new Jedis(address.getHost(), address.getPort());
				jedis.psubscribe(jedisPubSub, "__keyspace@0__:" + KEY_PREFIX + "*");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
