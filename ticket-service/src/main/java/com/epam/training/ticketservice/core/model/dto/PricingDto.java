package com.epam.training.ticketservice.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PricingDto {

    private Long id;

    private String name;

    private int amount;

    public PricingDto(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

}
