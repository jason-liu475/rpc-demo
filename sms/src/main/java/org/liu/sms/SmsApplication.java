package org.liu.sms;

import org.liu.config.annotations.EnableLiuRpc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;

@EnableLiuRpc
@SpringBootApplication
@PropertySource("classpath:/application.properties")
public class SmsApplication {

	public static void main(String[] args) throws Exception{
		//SpringApplication.run(SmsApplication.class, args);
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SmsApplication.class);
		context.start();
		System.in.read();
	}

}
