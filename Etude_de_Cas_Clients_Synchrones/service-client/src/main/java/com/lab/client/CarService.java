package com.lab.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CarService {
    private final RestTemplate restTemplate;
    private final CarFeignClient carFeignClient;
    private final WebClient.Builder webClientBuilder;

    public CarService(RestTemplate restTemplate, CarFeignClient carFeignClient, WebClient.Builder webClientBuilder) {
        this.restTemplate = restTemplate;
        this.carFeignClient = carFeignClient;
        this.webClientBuilder = webClientBuilder;
    }

    // RestTemplate approach
    public Car getCarByRestTemplate(Integer clientId) {
        String url = "http://SERVICE-VOITURE/api/cars/byClient/" + clientId;
        return restTemplate.getForObject(url, Car.class);
    }

    // Feign approach
    public Car getCarByFeign(Integer clientId) {
        return carFeignClient.getCarByClient(clientId);
    }

    // WebClient approach (synchronous with block())
    public Car getCarByWebClient(Integer clientId) {
        String url = "http://SERVICE-VOITURE/api/cars/byClient/" + clientId;
        return webClientBuilder
                .build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Car.class)
                .block();
    }
}
