package com.lab.voiture;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    private Integer id;
    private String marque;
    private String modele;
    private Integer clientId;
}
