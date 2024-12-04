package com.epam.training.ticketservice.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto {

    private Long id;

    private String name;

    private int rows;

    private int columns;

    private int seats;

    private Set<PricingDto> pricingComponents;

    public RoomDto(String name, int rows, int columns, int seats, Set<PricingDto> pricingComponents) {
        this.name = name;
        this.rows = rows;
        this.columns = columns;
        this.seats = seats;
        this.pricingComponents = pricingComponents;
    }

}
