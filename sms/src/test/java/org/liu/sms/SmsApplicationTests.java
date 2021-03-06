package org.liu.sms;

import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.liu.service.SmsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SmsApplication.class)
public class SmsApplicationTests {
	@Autowired
	private SmsService smsService;

	@Test
	@SneakyThrows
	public void test() {
		smsService.sendMessage("10086","junit");
		System.in.read();
	}
}
