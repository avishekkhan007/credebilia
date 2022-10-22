package com.gok.sbi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GoKApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx= SpringApplication.run(GoKApplication.class, args);
		ctx.close();
	}

}
