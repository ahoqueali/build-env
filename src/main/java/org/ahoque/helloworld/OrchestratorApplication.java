package org.ahoque.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

@SpringBootApplication
@RestController
public class OrchestratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrchestratorApplication.class, args);
	}

	@GetMapping("/hello")
	public String home() throws URISyntaxException {

		HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI("https://postman-echo.com/get"))
				.headers("key1", "value1", "key2", "value2")
				.GET()
				.build();


		return "Hello Docker World";
	}

}

