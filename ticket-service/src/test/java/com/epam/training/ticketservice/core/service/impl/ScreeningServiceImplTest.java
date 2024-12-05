package com.epam.training.ticketservice.core.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.Movie;
import com.epam.training.ticketservice.core.model.Room;
import com.epam.training.ticketservice.core.model.Screening;
import com.epam.training.ticketservice.core.model.dto.MovieDto;
import com.epam.training.ticketservice.core.model.dto.RoomDto;
import com.epam.training.ticketservice.core.model.dto.ScreeningDto;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import com.epam.training.ticketservice.core.repository.ScreeningRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

class ScreeningServiceImplTest {

    @Mock
    private ScreeningRepository screeningRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private ScreeningServiceImpl screeningService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateScreening_WhenAdminPrivilegesAreValidAndNoConflicts() {
        // Arrange
        String movieTitle = "Inception";
        String roomName = "Room 1";
        LocalDateTime startTime = LocalDateTime.now();
        Movie movie = new Movie(movieTitle, "Action", 120);
        MovieDto movieDto = new MovieDto(movieTitle, "Action", 120, null);
        Room room = new Room(roomName, 10, 10);
        RoomDto roomDto = new RoomDto(roomName, 10, 10, 100, null);
        Screening screening = new Screening(movie, room, startTime);
        ScreeningDto screeningDto = new ScreeningDto(movieDto, roomDto, startTime, null);

        when(userService.checkAdminPrivileges()).thenReturn(Result.success(null));
        when(movieRepository.findByTitle(movieTitle)).thenReturn(Optional.of(movie));
        when(roomRepository.findByName(roomName)).thenReturn(Optional.of(room));
        when(screeningRepository.findByRoom(room)).thenReturn(List.of());
        when(screeningRepository.save(any(Screening.class))).thenReturn(screening);
        when(objectMapper.convertValue(screening, ScreeningDto.class)).thenReturn(screeningDto);

        // Act
        Result<ScreeningDto> result = screeningService.createScreening(movieTitle, roomName, startTime);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(movieTitle, result.getData().getMovie().getTitle());
        verify(screeningRepository, times(1)).save(any(Screening.class));
    }

    @Test
    void testCreateScreening_WhenAdminPrivilegesAreInvalid() {
        // Arrange
        String movieTitle = "Inception";
        String roomName = "Room 1";
        LocalDateTime startTime = LocalDateTime.now();

        when(userService.checkAdminPrivileges()).thenReturn(Result.failure("Error: Admin privileges are required."));

        // Act
        Result<ScreeningDto> result = screeningService.createScreening(movieTitle, roomName, startTime);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Admin privileges are required.", result.getMessage());
        verify(screeningRepository, never()).save(any(Screening.class));
    }

    @Test
    void testCreateScreening_WhenMovieDoesNotExist() {
        // Arrange
        String movieTitle = "Inception";
        String roomName = "Room 1";
        LocalDateTime startTime = LocalDateTime.now();

        when(userService.checkAdminPrivileges()).thenReturn(Result.success(null));
        when(movieRepository.findByTitle(movieTitle)).thenReturn(Optional.empty());

        // Act
        Result<ScreeningDto> result = screeningService.createScreening(movieTitle, roomName, startTime);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Movie does not exist.", result.getMessage());
        verify(screeningRepository, never()).save(any(Screening.class));
    }

    @Test
    void testCreateScreening_WhenRoomDoesNotExist() {
        // Arrange
        String movieTitle = "Inception";
        String roomName = "Room 1";
        LocalDateTime startTime = LocalDateTime.now();
        Movie movie = new Movie(movieTitle, "Action", 120);

        when(userService.checkAdminPrivileges()).thenReturn(Result.success(null));
        when(movieRepository.findByTitle(movieTitle)).thenReturn(Optional.of(movie));
        when(roomRepository.findByName(roomName)).thenReturn(Optional.empty());

        // Act
        Result<ScreeningDto> result = screeningService.createScreening(movieTitle, roomName, startTime);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Room does not exist.", result.getMessage());
        verify(screeningRepository, never()).save(any(Screening.class));
    }

    @Test
    void testGetScreenings_WhenScreeningsExist() {
        // Arrange
        Movie movie = new Movie("Inception", "Action", 120);
        MovieDto movieDto = new MovieDto("Inception", "Action", 120, null);
        Room room = new Room("Room 1", 10, 10);
        RoomDto roomDto = new RoomDto("Room 1", 10, 10, 100, null);
        Screening screening = new Screening(movie, room, LocalDateTime.now());
        ScreeningDto screeningDto = new ScreeningDto(movieDto, roomDto, LocalDateTime.now(), null);

        when(screeningRepository.findAll()).thenReturn(List.of(screening));
        when(objectMapper.convertValue(screening, ScreeningDto.class)).thenReturn(screeningDto);

        // Act
        Result<List<ScreeningDto>> result = screeningService.getScreenings();

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
    }

    @Test
    void testGetScreenings_WhenNoScreeningsExist() {
        // Arrange
        when(screeningRepository.findAll()).thenReturn(List.of());

        // Act
        Result<List<ScreeningDto>> result = screeningService.getScreenings();

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("There are no screenings", result.getMessage());
    }

    @Test
    void testDeleteScreening_WhenScreeningExists() {
        // Arrange
        String movieTitle = "Inception";
        String roomName = "Room 1";
        LocalDateTime startTime = LocalDateTime.now();
        Movie movie = new Movie(movieTitle, "Action", 120);
        Room room = new Room(roomName, 10, 10);
        MovieDto movieDto = new MovieDto(movieTitle, "Action", 120, null);
        RoomDto roomDto = new RoomDto(roomName, 10, 10, 100, null);
        Screening screening = new Screening(movie, room, startTime);
        ScreeningDto screeningDto = new ScreeningDto(movieDto, roomDto, startTime, null);

        when(userService.checkAdminPrivileges()).thenReturn(Result.success(null));
        when(screeningRepository.findByMovieTitleAndRoomNameAndStartTime(movieTitle, roomName, startTime))
                .thenReturn(Optional.of(screening));
        when(objectMapper.convertValue(screening, ScreeningDto.class)).thenReturn(screeningDto);

        // Act
        Result<ScreeningDto> result = screeningService.deleteScreening(movieTitle, roomName, startTime);

        // Assert
        assertTrue(result.isSuccess());
        verify(screeningRepository, times(1)).delete(screening);
    }

    @Test
    void testDeleteScreening_WhenScreeningDoesNotExist() {
        // Arrange
        String movieTitle = "Inception";
        String roomName = "Room 1";
        LocalDateTime startTime = LocalDateTime.now();

        when(userService.checkAdminPrivileges()).thenReturn(Result.success(null));
        when(screeningRepository.findByMovieTitleAndRoomNameAndStartTime(movieTitle, roomName, startTime))
                .thenReturn(Optional.empty());

        // Act
        Result<ScreeningDto> result = screeningService.deleteScreening(movieTitle, roomName, startTime);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Screening does not exist.", result.getMessage());
        verify(screeningRepository, never()).delete(any(Screening.class));
    }

    @Test
    void testCheckScheduleConflicts_WhenNoConflictsExist() {
        // Arrange
        Room room = new Room("Room 1", 10, 10);
        LocalDateTime startTime = LocalDateTime.of(2023, 12, 5, 14, 0);
        when(screeningRepository.findByRoom(room)).thenReturn(List.of());

        // Act
        Result<Void> result = invokeCheckScheduleConflicts(room, startTime);

        // Assert
        assertTrue(result.isSuccess());
    }

    @Test
    void testCheckScheduleConflicts_WhenThereIsAnOverlappingScreening() {
        // Arrange
        Room room = new Room("Room 1", 10, 10);
        LocalDateTime startTime = LocalDateTime.of(2023, 12, 5, 14, 30);
        Movie movie = new Movie("Movie 1", "Action", 120);
        Screening existingScreening = new Screening(movie, room, LocalDateTime.of(2023, 12, 5, 13, 30));

        when(screeningRepository.findByRoom(room)).thenReturn(List.of(existingScreening));

        // Act
        Result<Void> result = invokeCheckScheduleConflicts(room, startTime);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("There is an overlapping screening", result.getMessage());
    }

    @Test
    void testCheckScheduleConflicts_WhenStartTimeIsInBreakPeriod() {
        // Arrange
        Room room = new Room("Room 1", 10, 10);
        LocalDateTime existingStartTime = LocalDateTime.of(2023, 12, 5, 13, 30);
        LocalDateTime existingEndTime = existingStartTime.plusMinutes(120);
        LocalDateTime breakEndTime = existingEndTime.plusMinutes(10);
        LocalDateTime startTime = LocalDateTime.of(2023, 12, 5, 15, 35);

        Movie movie = new Movie("Movie 1", "Action", 120);
        Screening existingScreening = new Screening(movie, room, existingStartTime);

        when(screeningRepository.findByRoom(room)).thenReturn(List.of(existingScreening));

        // Act
        Result<Void> result = invokeCheckScheduleConflicts(room, startTime);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("This would start in the break period after another screening in this room", result.getMessage());
    }

    @Test
    void testCheckScheduleConflicts_WhenMultipleScreeningsExistButNoConflicts() {
        // Arrange
        Room room = new Room("Room 1", 10, 10);
        LocalDateTime startTime = LocalDateTime.of(2023, 12, 5, 17, 0);
        Movie movie1 = new Movie("Movie 1", "Action", 120);
        Movie movie2 = new Movie("Movie 2", "Comedy", 90);
        Screening screening1 = new Screening(movie1, room, LocalDateTime.of(2023, 12, 5, 10, 0));
        Screening screening2 = new Screening(movie2, room, LocalDateTime.of(2023, 12, 5, 13, 0));

        when(screeningRepository.findByRoom(room)).thenReturn(List.of(screening1, screening2));

        // Act
        Result<Void> result = invokeCheckScheduleConflicts(room, startTime);

        // Assert
        assertTrue(result.isSuccess());
    }

    @Test
    void testCreateScreening_WhenScheduleCheckFails() {
        // Arrange
        String movieTitle = "Inception";
        String roomName = "Room 1";
        LocalDateTime startTime = LocalDateTime.of(2023, 12, 5, 14, 30);
        Movie movie = new Movie(movieTitle, "Action", 120);
        Room room = new Room(roomName, 10, 10);
        Screening conflictingScreening = new Screening(movie, room, LocalDateTime.of(2023, 12, 5, 13, 30));

        when(userService.checkAdminPrivileges()).thenReturn(Result.success(null));
        when(movieRepository.findByTitle(movieTitle)).thenReturn(Optional.of(movie));
        when(roomRepository.findByName(roomName)).thenReturn(Optional.of(room));
        when(screeningRepository.findByRoom(room)).thenReturn(List.of(conflictingScreening));

        // Act
        Result<ScreeningDto> result = screeningService.createScreening(movieTitle, roomName, startTime);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("There is an overlapping screening", result.getMessage());
        verify(screeningRepository, never()).save(any(Screening.class));
    }

    @Test
    void testDeleteScreening_WhenAdminPrivilegesAreInvalid() {
        // Arrange
        String movieTitle = "Inception";
        String roomName = "Room 1";
        LocalDateTime startTime = LocalDateTime.now();

        when(userService.checkAdminPrivileges()).thenReturn(Result.failure("Error: Admin privileges are required."));

        // Act
        Result<ScreeningDto> result = screeningService.deleteScreening(movieTitle, roomName, startTime);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Admin privileges are required.", result.getMessage());
        verify(screeningRepository, never()).delete(any(Screening.class));
    }

    private Result<Void> invokeCheckScheduleConflicts(Room room, LocalDateTime startTime) {
        try {
            // Access private method via reflection
            Method method = ScreeningServiceImpl.class.getDeclaredMethod("checkScheduleConflicts", Room.class, LocalDateTime.class);
            method.setAccessible(true);
            return (Result<Void>) method.invoke(screeningService, room, startTime);
        } catch (Exception e) {
            throw new RuntimeException("Reflection error: " + e.getMessage(), e);
        }
    }
}
