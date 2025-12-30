package com.lab.voiture;

import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class CarRepository {
    private static final Map<Integer, Car> cars = new HashMap<>();

    static {
        cars.put(1, new Car(10, "Toyota", "Yaris", 1));
        cars.put(2, new Car(20, "Honda", "Civic", 2));
        cars.put(3, new Car(30, "BMW", "320i", 3));
        cars.put(4, new Car(40, "Audi", "A4", 4));
        cars.put(5, new Car(50, "Mercedes", "C-Class", 5));
    }

    public Optional<Car> findByClientId(Integer clientId) {
        return Optional.ofNullable(cars.get(clientId));
    }
}
