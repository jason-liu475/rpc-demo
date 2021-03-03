package org.liu.order.service.Impl;

import org.liu.config.spring.annotations.LiuRpcReference;
import org.liu.order.service.OrderService;
import org.liu.service.SmsService;

public class OrderServiceImpl implements OrderService {

	@LiuRpcReference
	private SmsService smsService;
	@Override
	public void createOrder() {
		smsService.sendMessage("10086","手机已欠费");
	}
}
