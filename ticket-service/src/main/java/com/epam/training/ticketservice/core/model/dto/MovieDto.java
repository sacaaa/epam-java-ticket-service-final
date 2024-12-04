package com.epam.training.ticketservice.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto {

    private Long id;

    private String title;

    private String genre;

    private int length;

    private Set<PricingDto> pricingComponents;

    public MovieDto(String title, String genre, int length, Set<PricingDto> pricingComponents) {
        this.title = title;
        this.genre = genre;
        this.length = length;
        this.pricingComponents = pricingComponents;
    }

}
