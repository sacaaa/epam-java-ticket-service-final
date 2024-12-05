package com.epam.training.ticketservice.core.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.Booking;
import com.epam.training.ticketservice.core.model.Screening;
import com.epam.training.ticketservice.core.model.User;
import com.epam.training.ticketservice.core.model.dto.BookingDto;
import com.epam.training.ticketservice.core.model.dto.UserDto;
import com.epam.training.ticketservice.core.repository.BookingRepository;
import com.epam.training.ticketservice.core.repository.ScreeningRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ScreeningRepository screeningRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBooking_WhenUserIsAuthenticatedAndScreeningExists() {
        // Arrange
        String username = "user";
        String movieTitle = "Movie";
        String roomName = "Room";
        LocalDateTime startTime = LocalDateTime.now();
        List<String> seats = List.of("1,1");
        UserDto userDto = new UserDto(username, null, "USER");
        Screening screening = new Screening();

        when(userService.getAuthenticatedUser(username))
                .thenReturn(Result.success(userDto));
        when(screeningRepository.findByMovieTitleAndRoomNameAndStartTime(movieTitle, roomName, startTime))
                .thenReturn(Optional.of(screening));
        when(bookingRepository.existsByScreeningAndSeatsContaining(eq(screening), anyString()))
                .thenReturn(false);

        // Act
        Result<BookingDto> result = bookingService.createBooking(username, movieTitle, roomName, startTime, seats);

        // Assert
        assertTrue(result.isSuccess());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_WhenUserIsNotAuthenticated() {
        // Arrange
        String username = "user";
        String movieTitle = "Movie";
        String roomName = "Room";
        LocalDateTime startTime = LocalDateTime.now();
        List<String> seats = List.of("1,1");

        when(userService.getAuthenticatedUser(username))
                .thenReturn(Result.failure("User is not authenticated"));

        // Act
        Result<BookingDto> result = bookingService.createBooking(username, movieTitle, roomName, startTime, seats);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("User is not authenticated", result.getMessage());
        verifyNoInteractions(screeningRepository, bookingRepository);
    }

    @Test
    void testGetBookingsByUser_WhenUserHasBookings() {
        // Arrange
        String username = "user";
        UserDto userDto = new UserDto(username, null, "USER");
        User user = objectMapper.convertValue(userDto, User.class);
        Booking booking = new Booking(user, new Screening(), List.of("1,1"), 1500, LocalDateTime.now());

        when(userService.getAuthenticatedUser(username))
                .thenReturn(Result.success(userDto));
        when(bookingRepository.findAllByUser(user))
                .thenReturn(List.of(booking));

        // Act
        Result<List<BookingDto>> result = bookingService.getBookingsByUser(username);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
    }

    @Test
    void testGetBookingsByUser_WhenUserHasNoBookings() {
        // Arrange
        String username = "user";
        UserDto userDto = new UserDto(username, null, "USER");
        User user = objectMapper.convertValue(userDto, User.class);

        when(userService.getAuthenticatedUser(username))
                .thenReturn(Result.success(userDto));
        when(bookingRepository.findAllByUser(user))
                .thenReturn(Collections.emptyList());

        // Act
        Result<List<BookingDto>> result = bookingService.getBookingsByUser(username);

        // Assert
        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    void testProcessBooking_WhenSeatIsAlreadyTaken() {
        // Arrange
        String username = "user";
        Screening screening = new Screening();
        List<String> seats = List.of("1,1", "1,2");
        UserDto userDto = new UserDto(username, null, "USER");

        when(bookingRepository.existsByScreeningAndSeatsContaining(screening, "1,1")).thenReturn(true);

        // Act
        Result<BookingDto> result = invokeProcessBooking(userDto, screening, seats);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Seat (1,1) is already taken", result.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testGetBookingsByUser_WhenUserIsNotAuthenticated() {
        // Arrange
        String username = "user";

        when(userService.getAuthenticatedUser(username))
                .thenReturn(Result.failure("User is not authenticated"));

        // Act
        Result<List<BookingDto>> result = bookingService.getBookingsByUser(username);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("User is not authenticated", result.getMessage());
        verifyNoInteractions(bookingRepository);
    }


    private Result<BookingDto> invokeProcessBooking(UserDto user, Screening screening, List<String> seats) {
        try {
            // Access private method via reflection
            Method method = BookingServiceImpl.class.getDeclaredMethod("processBooking", UserDto.class, Screening.class, List.class);
            method.setAccessible(true);
            return (Result<BookingDto>) method.invoke(bookingService, user, screening, seats);
        } catch (Exception e) {
            throw new RuntimeException("Reflection error: " + e.getMessage(), e);
        }
    }
}
