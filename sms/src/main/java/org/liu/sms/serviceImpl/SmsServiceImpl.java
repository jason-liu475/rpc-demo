package org.liu.sms.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.liu.config.spring.annotations.LiuRpcService;
import org.liu.service.SmsService;


@Slf4j
@LiuRpcService
public class SmsServiceImpl implements SmsService {
	@Override
	public String sendMessage(String phone,String content) {
		log.info("发送到：{}，短信内容：{}",phone,content);
		return "发送成功";
	}
}
