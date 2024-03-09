package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication 이 있는 패키지와 하위 패키지에 있는걸 다 컴포넌트 스캔해서 스프링 빈에 자동 등록
@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		/*
		Hello hello = new Hello();
		hello.setData("hi");
		String data = hello.getData();
		System.out.println(data);
		 */

		SpringApplication.run(JpashopApplication.class, args);
	}

}
