package com.epam.training.ticketservice.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private Long id;

    private UserDto user;

    private ScreeningDto screening;

    private List<String> seats;

    private int price;

    private LocalDateTime bookingTime;

    public BookingDto(UserDto user, ScreeningDto screening, List<String> seats, int price, LocalDateTime bookingTime) {
        this.user = user;
        this.screening = screening;
        this.seats = seats;
        this.price = price;
        this.bookingTime = bookingTime;
    }

}
