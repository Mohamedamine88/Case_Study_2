package com.lab.voiture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = "*")
public class CarController {
    
    @Autowired
    private CarRepository carRepository;

    @GetMapping("/byClient/{clientId}")
    public ResponseEntity<Car> getCarByClient(@PathVariable Integer clientId) {
        // Simuler un délai de traitement (20ms pour observable)
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return carRepository.findByClientId(clientId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
