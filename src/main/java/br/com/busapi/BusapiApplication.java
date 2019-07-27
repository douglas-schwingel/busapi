package br.com.busapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport
public class BusapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusapiApplication.class, args);
	}

}
