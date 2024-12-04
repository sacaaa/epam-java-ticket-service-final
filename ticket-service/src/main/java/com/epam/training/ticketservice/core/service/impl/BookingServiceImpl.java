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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

    @Lazy
    @Autowired
    private UserServiceImpl userService;

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

    private int calculatePrice(int seatCount) {
        return seatCount * SEAT_PRICE;
    }

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
