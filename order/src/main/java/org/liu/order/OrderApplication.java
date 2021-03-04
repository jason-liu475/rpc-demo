package org.liu.order;

import java.util.concurrent.CyclicBarrier;

import lombok.extern.slf4j.Slf4j;
import org.liu.config.spring.annotations.EnableLiuRpc;
import org.liu.order.service.OrderService;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@EnableLiuRpc
@SpringBootApplication
@PropertySource("classpath:/application.properties")
public class OrderApplication {

	public static void main(String[] args) {
//		SpringApplication.run(OrderApplication.class, args);
		final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(OrderApplication.class);
		context.start();

		final CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
		for (int i = 0; i < 10; i++) {
			new Thread(() ->{
				try {
					cyclicBarrier.await();
					OrderService orderService = context.getBean(OrderService.class);
					orderService.createOrder("买一瓶快乐水");
				}catch (Exception e){
					log.error("exception",e);
				}
			}).start();
		}
	}

}
