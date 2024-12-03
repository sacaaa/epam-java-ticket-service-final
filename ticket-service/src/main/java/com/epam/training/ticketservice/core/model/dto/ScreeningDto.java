package com.epam.training.ticketservice.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreeningDto {

    private Long id;

    private MovieDto movie;

    private RoomDto room;

    private LocalDateTime startTime;

    public ScreeningDto(MovieDto movie, RoomDto room, LocalDateTime startTime) {
        this.movie = movie;
        this.room = room;
        this.startTime = startTime;
    }
}
