package io.hahn_software.emrs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class EmrsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmrsApplication.class, args);
	}

}
