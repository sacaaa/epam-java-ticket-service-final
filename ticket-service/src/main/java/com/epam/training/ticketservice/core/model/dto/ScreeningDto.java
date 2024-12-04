package com.epam.training.ticketservice.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreeningDto {

    private Long id;

    private MovieDto movie;

    private RoomDto room;

    private LocalDateTime startTime;

    private Set<PricingDto> pricingComponents;

    public ScreeningDto(MovieDto movie, RoomDto room, LocalDateTime startTime, Set<PricingDto> pricingComponents) {
        this.movie = movie;
        this.room = room;
        this.startTime = startTime;
        this.pricingComponents = pricingComponents;
    }

}
