package com.epam.training.ticketservice.core.service.impl;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.Booking;
import com.epam.training.ticketservice.core.model.Screening;
import com.epam.training.ticketservice.core.model.User;
import com.epam.training.ticketservice.core.model.dto.BookingDto;
import com.epam.training.ticketservice.core.model.dto.UserDto;
import com.epam.training.ticketservice.core.repository.BookingRepository;
import com.epam.training.ticketservice.core.repository.ScreeningRepository;
import com.epam.training.ticketservice.core.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ScreeningRepository screeningRepository;
    
    private final ObjectMapper objectMapper;

    private final UserServiceImpl userService;

    /**
     * The price per seat for a booking.
     */
    private static final int SEAT_PRICE = 1500;

    @Override
    public Result<BookingDto> createBooking(String username,
                                            String movieTitle,
                                            String roomName,
                                            LocalDateTime startTime,
                                            List<String> seats) {
        Result<UserDto> userResult = userService.getAuthenticatedUser(username);
        if (!userResult.isSuccess()) {
            return Result.failure(userResult.getMessage());
        }

        return screeningRepository.findByMovieTitleAndRoomNameAndStartTime(movieTitle, roomName, startTime)
                .map(screening -> processBooking(userResult.getData(), screening, seats))
                .orElseGet(() -> Result.failure("Error: Screening does not exist."));
    }

    /**
     * Processes a booking request by validating the selected seats, calculating the total price,
     * and saving the booking.
     *
     * @param user      The user making the booking.
     * @param screening The screening for which the booking is being made.
     * @param seats     The list of seat identifiers to book.
     * @return A {@code Result} containing the created {@code BookingDto} or an error message if the booking fails.
     */
    private Result<BookingDto> processBooking(UserDto user, Screening screening, List<String> seats) {
        for (String seat : seats) {
            if (bookingRepository.existsByScreeningAndSeatsContaining(screening, seat)) {
                return Result.failure("Seat (" + seat + ") is already taken");
            }
        }

        int price = calculatePrice(seats.size());
        Booking booking = createAndSaveBooking(user, screening, seats, price);
        return Result.success(objectMapper.convertValue(booking, BookingDto.class));
    }

    /**
     * Calculates the total price for a booking based on the number of seats.
     *
     * @param seatCount The number of seats in the booking.
     * @return The total price for the booking.
     */
    private int calculatePrice(int seatCount) {
        return seatCount * SEAT_PRICE;
    }

    /**
     * Creates and saves a booking entity.
     *
     * @param user      The user making the booking.
     * @param screening The screening for which the booking is being made.
     * @param seats     The list of seat identifiers to book.
     * @param price     The total price for the booking.
     * @return The saved {@code Booking} entity.
     */
    private Booking createAndSaveBooking(UserDto user, Screening screening, List<String> seats, int price) {
        User userEntity = objectMapper.convertValue(user, User.class);
        Booking booking = new Booking(userEntity, screening, seats, price, LocalDateTime.now());
        bookingRepository.save(booking);
        return booking;
    }

    @Override
    public Result<List<BookingDto>> getBookingsByUser(String username) {
        Result<UserDto> userResult = userService.getAuthenticatedUser(username);
        if (!userResult.isSuccess()) {
            return Result.failure(userResult.getMessage());
        }

        User userEntity = objectMapper.convertValue(userResult.getData(), User.class);
        List<BookingDto> bookingDtos = bookingRepository.findAllByUser(userEntity).stream()
                .map(booking -> objectMapper.convertValue(booking, BookingDto.class))
                .collect(Collectors.toList());

        return Result.success(bookingDtos);
    }

}
