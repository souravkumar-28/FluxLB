package com.sourav.fluxlb;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableScheduling
public class FluxlbApplication {

	public static void main(String[] args) {
		SpringApplication.run(FluxlbApplication.class, args);
	}
}