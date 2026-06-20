package com.mishes.pago;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients//para la comunicacion interna entre microservicios
public class PagoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PagoApplication.class, args);
	}

}
