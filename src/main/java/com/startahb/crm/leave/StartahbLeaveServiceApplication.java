package com.startahb.crm.leave;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@EnableDiscoveryClient
@SpringBootApplication
public class StartahbLeaveServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StartahbLeaveServiceApplication.class, args);
	}

}
