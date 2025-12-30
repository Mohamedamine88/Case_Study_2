package com.lab.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "*")
public class ClientController {
    
    @Autowired
    private CarService carService;

    @GetMapping("/{id}/car/rest")
    public ResponseEntity<Car> getCarByRest(@PathVariable Integer id) {
        try {
            Car car = carService.getCarByRestTemplate(id);
            return ResponseEntity.ok(car);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}/car/feign")
    public ResponseEntity<Car> getCarByFeign(@PathVariable Integer id) {
        try {
            Car car = carService.getCarByFeign(id);
            return ResponseEntity.ok(car);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}/car/webclient")
    public ResponseEntity<Car> getCarByWebClient(@PathVariable Integer id) {
        try {
            Car car = carService.getCarByWebClient(id);
            return ResponseEntity.ok(car);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service Client is up");
    }
}
