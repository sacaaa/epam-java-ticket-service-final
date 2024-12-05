package com.epam.training.ticketservice.core.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.Pricing;
import com.epam.training.ticketservice.core.model.Movie;
import com.epam.training.ticketservice.core.model.Room;
import com.epam.training.ticketservice.core.model.Screening;
import com.epam.training.ticketservice.core.model.dto.PricingDto;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import com.epam.training.ticketservice.core.repository.PricingRepository;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import com.epam.training.ticketservice.core.repository.ScreeningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

class PricingServiceImplTest {

    @Mock
    private PricingRepository pricingRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ScreeningRepository screeningRepository;

    @InjectMocks
    private PricingServiceImpl pricingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePricingComponent_WhenComponentDoesNotExist() {
        // Arrange
        String name = "Premium";
        int amount = 500;

        when(pricingRepository.findByName(name)).thenReturn(Optional.empty());

        // Act
        Result<Void> result = pricingService.createPricingComponent(name, amount);

        // Assert
        assertTrue(result.isSuccess());
        verify(pricingRepository, times(1)).save(any(Pricing.class));
    }

    @Test
    void testCreatePricingComponent_WhenComponentAlreadyExists() {
        // Arrange
        String name = "Premium";

        when(pricingRepository.findByName(name)).thenReturn(Optional.of(new Pricing(name, 500)));

        // Act
        Result<Void> result = pricingService.createPricingComponent(name, 500);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Pricing component already exists.", result.getMessage());
        verify(pricingRepository, never()).save(any(Pricing.class));
    }

    @Test
    void testUpdateBasePrice() {
        // Act
        Result<Void> result = pricingService.updateBasePrice(2000);

        // Assert
        assertTrue(result.isSuccess());
    }

    @Test
    void testAttachPricingComponentToMovie_WhenComponentAndMovieExist() {
        // Arrange
        String pricingComponentName = "Premium";
        String movieTitle = "Inception";
        Pricing pricing = new Pricing(pricingComponentName, 500);
        Movie movie = new Movie(movieTitle, "Action", 120);

        when(pricingRepository.findByName(pricingComponentName)).thenReturn(Optional.of(pricing));
        when(movieRepository.findByTitle(movieTitle)).thenReturn(Optional.of(movie));

        // Act
        Result<Void> result = pricingService.attachPricingComponentToMovie(pricingComponentName, movieTitle);

        // Assert
        assertTrue(result.isSuccess());
        assertTrue(movie.getPricingComponents().contains(pricing));
        verify(movieRepository, times(1)).save(movie);
    }

    @Test
    void testAttachPricingComponentToMovie_WhenComponentDoesNotExist() {
        // Arrange
        String pricingComponentName = "Premium";
        String movieTitle = "Inception";

        when(pricingRepository.findByName(pricingComponentName)).thenReturn(Optional.empty());

        // Act
        Result<Void> result = pricingService.attachPricingComponentToMovie(pricingComponentName, movieTitle);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Pricing component does not exist.", result.getMessage());
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void testAttachPricingComponentToRoom_WhenComponentAndRoomExist() {
        // Arrange
        String pricingComponentName = "Premium";
        String roomName = "Room 1";
        Pricing pricing = new Pricing(pricingComponentName, 500);
        Room room = new Room(roomName, 10, 10);

        when(pricingRepository.findByName(pricingComponentName)).thenReturn(Optional.of(pricing));
        when(roomRepository.findByName(roomName)).thenReturn(Optional.of(room));

        // Act
        Result<Void> result = pricingService.attachPricingComponentToRoom(pricingComponentName, roomName);

        // Assert
        assertTrue(result.isSuccess());
        assertTrue(room.getPricingComponents().contains(pricing));
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void testCalculatePrice_WhenScreeningExists() {
        // Arrange
        String movieTitle = "Inception";
        String roomName = "Room 1";
        LocalDateTime startDateTime = LocalDateTime.now();
        List<String> seats = List.of("A1", "A2");
        Pricing pricing = new Pricing("Premium", 500);

        Screening screening = new Screening();
        screening.setMovie(new Movie(movieTitle, "Action", 120));
        screening.setRoom(new Room(roomName, 10, 10));
        screening.setPricingComponents(new HashSet<>()); // Initialize pricingComponents
        screening.getPricingComponents().add(pricing);

        when(screeningRepository.findByMovieTitleAndRoomNameAndStartTime(movieTitle, roomName, startDateTime))
                .thenReturn(Optional.of(screening));

        // Act
        Result<Integer> result = pricingService.calculatePrice(movieTitle, roomName, startDateTime, seats);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals((1500 + 500) * seats.size(), result.getData());
    }


    @Test
    void testCalculatePrice_WhenScreeningDoesNotExist() {
        // Arrange
        String movieTitle = "Inception";
        String roomName = "Room 1";
        LocalDateTime startDateTime = LocalDateTime.now();
        List<String> seats = List.of("A1", "A2");

        when(screeningRepository.findByMovieTitleAndRoomNameAndStartTime(movieTitle, roomName, startDateTime))
                .thenReturn(Optional.empty());

        // Act
        Result<Integer> result = pricingService.calculatePrice(movieTitle, roomName, startDateTime, seats);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Screening does not exist.", result.getMessage());
    }

    @Test
    void testAttachPricingComponentToScreening_WhenComponentAndScreeningExist() {
        // Arrange
        String pricingComponentName = "Premium";
        String movieTitle = "Inception";
        String roomName = "Room 1";
        LocalDateTime startTime = LocalDateTime.now();
        Pricing pricing = new Pricing(pricingComponentName, 500);
        Screening screening = new Screening();
        screening.setPricingComponents(new HashSet<>());

        when(pricingRepository.findByName(pricingComponentName)).thenReturn(Optional.of(pricing));
        when(screeningRepository.findByMovieTitleAndRoomNameAndStartTime(movieTitle, roomName, startTime))
                .thenReturn(Optional.of(screening));

        // Act
        Result<Void> result = pricingService.attachPricingComponentToScreening(pricingComponentName, movieTitle, roomName, startTime);

        // Assert
        assertTrue(result.isSuccess());
        assertTrue(screening.getPricingComponents().contains(pricing));
        verify(screeningRepository, times(1)).save(screening);
    }

    @Test
    void testAttachPricingComponentToScreening_WhenPricingComponentDoesNotExist() {
        // Arrange
        String pricingComponentName = "Premium";
        String movieTitle = "Inception";
        String roomName = "Room 1";
        LocalDateTime startTime = LocalDateTime.now();

        when(pricingRepository.findByName(pricingComponentName)).thenReturn(Optional.empty());

        // Act
        Result<Void> result = pricingService.attachPricingComponentToScreening(pricingComponentName, movieTitle, roomName, startTime);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Pricing component does not exist.", result.getMessage());
        verify(screeningRepository, never()).save(any(Screening.class));
    }

    @Test
    void testAttachPricingComponentToScreening_WhenScreeningDoesNotExist() {
        // Arrange
        String pricingComponentName = "Premium";
        String movieTitle = "Inception";
        String roomName = "Room 1";
        LocalDateTime startTime = LocalDateTime.now();
        Pricing pricing = new Pricing(pricingComponentName, 500);
        PricingDto pricingDto = new PricingDto(pricingComponentName, 500);

        when(pricingRepository.findByName(pricingComponentName)).thenReturn(Optional.of(pricing));
        when(screeningRepository.findByMovieTitleAndRoomNameAndStartTime(movieTitle, roomName, startTime))
                .thenReturn(Optional.empty());

        // Act
        Result<Void> result = pricingService.attachPricingComponentToScreening(pricingComponentName, movieTitle, roomName, startTime);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Screening does not exist.", result.getMessage());
        verify(screeningRepository, never()).save(any(Screening.class));
    }

    @Test
    void testAttachPricingComponentToMovie_WhenMovieDoesNotExist() {
        // Arrange
        String pricingComponentName = "Premium";
        String movieTitle = "Inception";
        Pricing pricing = new Pricing(pricingComponentName, 500);

        when(pricingRepository.findByName(pricingComponentName)).thenReturn(Optional.of(pricing));
        when(movieRepository.findByTitle(movieTitle)).thenReturn(Optional.empty());

        // Act
        Result<Void> result = pricingService.attachPricingComponentToMovie(pricingComponentName, movieTitle);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Movie does not exist.", result.getMessage());
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void testAttachPricingComponentToRoom_WhenPricingComponentDoesNotExist() {
        // Arrange
        String pricingComponentName = "Premium";
        String roomName = "Room 1";

        when(pricingRepository.findByName(pricingComponentName)).thenReturn(Optional.empty());

        // Act
        Result<Void> result = pricingService.attachPricingComponentToRoom(pricingComponentName, roomName);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Pricing component does not exist.", result.getMessage());
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void testAttachPricingComponentToRoom_WhenRoomDoesNotExist() {
        // Arrange
        String pricingComponentName = "Premium";
        String roomName = "Room 1";
        Pricing pricing = new Pricing(pricingComponentName, 500);

        when(pricingRepository.findByName(pricingComponentName)).thenReturn(Optional.of(pricing));
        when(roomRepository.findByName(roomName)).thenReturn(Optional.empty());

        // Act
        Result<Void> result = pricingService.attachPricingComponentToRoom(pricingComponentName, roomName);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Room does not exist.", result.getMessage());
        verify(roomRepository, never()).save(any(Room.class));
    }

}
