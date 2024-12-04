package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    Result<BookingDto> createBooking(String username,
                                     String movieTitle,
                                     String roomName,
                                     LocalDateTime startTime,
                                     List<String> seats);

    Result<List<BookingDto>> getBookingsByUser(String username);

}
