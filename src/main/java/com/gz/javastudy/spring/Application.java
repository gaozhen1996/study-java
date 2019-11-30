package com.gz.javastudy.spring;

import com.gz.javastudy.spring.bean.AnnotationConfigApplicationContext;
import com.gz.javastudy.spring.context.ComponentScan;

@ComponentScan("com.gz.javastudy.spring")
public class Application {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context =
				new AnnotationConfigApplicationContext(Application.class);
		System.out.println(context);
		System.out.println(context.getBeanDefinition("config").isLazyInit());
	}
}
