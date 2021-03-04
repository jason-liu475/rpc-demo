package org.liu.order.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.liu.config.spring.annotations.LiuRpcReference;
import org.liu.order.service.OrderService;
import org.liu.service.SmsService;

import org.springframework.stereotype.Service;
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

	@LiuRpcReference(loadBalance = "RandomLoadBalance")
	private SmsService smsService;
	@Override
	public void createOrder(String msg) {
		log.info("订单创建成功：{}",msg);
		String result = smsService.sendMessage("10086", msg);
		log.info("smsService调用结果：{}",result);
	}
}
