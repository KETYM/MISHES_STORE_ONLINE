package com.producto.ms_producto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MsProductoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsProductoApplication.class, args);
	}

}
