package org.liu.sms.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.liu.service.SmsService;
import org.liu.utils.annotations.LiuRpcService;

@Slf4j
@LiuRpcService
public class SmsServiceImpl implements SmsService {
	@Override
	public boolean sendMessage(String content) {
		log.info("短信内容：{}",content);
		return false;
	}
}
