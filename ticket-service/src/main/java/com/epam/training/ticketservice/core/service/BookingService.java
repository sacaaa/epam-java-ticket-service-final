package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing movie bookings.
 */
public interface BookingService {

    /**
     * Creates a new booking for a user.
     *
     * @param username   The username of the person making the booking.
     * @param movieTitle The title of the movie being booked.
     * @param roomName   The name of the room where the screening is taking place.
     * @param startTime  The start time of the screening.
     * @param seats      The list of seats to be booked.
     * @return A {@code Result} containing the created {@code BookingDto} or an error message if the booking fails.
     */
    Result<BookingDto> createBooking(String username,
                                     String movieTitle,
                                     String roomName,
                                     LocalDateTime startTime,
                                     List<String> seats);

    /**
     * Retrieves all bookings made by a specific user.
     *
     * @param username The username of the user whose bookings are to be retrieved.
     * @return A {@code Result} containing a list of {@code BookingDto} objects representing the user's bookings,
     *         or an error message if retrieval fails.
     */
    Result<List<BookingDto>> getBookingsByUser(String username);

}
