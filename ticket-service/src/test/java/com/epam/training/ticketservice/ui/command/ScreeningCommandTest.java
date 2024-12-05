package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.service.impl.ScreeningServiceImpl;
import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.dto.MovieDto;
import com.epam.training.ticketservice.core.model.dto.RoomDto;
import com.epam.training.ticketservice.core.model.dto.ScreeningDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ScreeningCommandTest {

    @Mock
    private ScreeningServiceImpl screeningService;

    @InjectMocks
    private ScreeningCommand screeningCommand;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateScreening_Success() {
        // Arrange
        String movieTitle = "Inception";
        String roomName = "Room 1";
        String startTime = "2023-12-05 10:00";
        LocalDateTime parsedStartTime = LocalDateTime.parse(startTime, dateTimeFormatter);

        when(screeningService.createScreening(movieTitle, roomName, parsedStartTime))
                .thenReturn(Result.success(null));

        // Act
        String result = screeningCommand.createScreening(movieTitle, roomName, startTime);

        // Assert
        assertEquals("Screening created", result);
        verify(screeningService).createScreening(movieTitle, roomName, parsedStartTime);
    }

    @Test
    void testCreateScreening_Failure() {
        // Arrange
        String movieTitle = "Inception";
        String roomName = "Room 1";
        String startTime = "2023-12-05 10:00";
        LocalDateTime parsedStartTime = LocalDateTime.parse(startTime, dateTimeFormatter);

        when(screeningService.createScreening(movieTitle, roomName, parsedStartTime))
                .thenReturn(Result.failure("Error: Scheduling conflict"));

        // Act
        String result = screeningCommand.createScreening(movieTitle, roomName, startTime);

        // Assert
        assertEquals("Error: Scheduling conflict", result);
        verify(screeningService).createScreening(movieTitle, roomName, parsedStartTime);
    }

    @Test
    void testCreateScreening_InvalidDateFormat() {
        // Arrange
        String movieTitle = "Inception";
        String roomName = "Room 1";
        String startTime = "invalid-date";

        // Act
        String result = screeningCommand.createScreening(movieTitle, roomName, startTime);

        // Assert
        assertEquals("Error: Invalid date format. Use 'yyyy-MM-dd HH:mm'.", result);
        verifyNoInteractions(screeningService);
    }

    @Test
    void testListScreenings_Success() {
        // Arrange
        MovieDto movieDto = new MovieDto("Inception", "Action", 120, null);
        RoomDto roomDto = new RoomDto("Room 1", 10, 10, 100, null);
        ScreeningDto screeningDto = new ScreeningDto(movieDto, roomDto,
                LocalDateTime.of(2023, 12, 5, 10, 0), null);

        when(screeningService.getScreenings()).thenReturn(Result.success(List.of(screeningDto)));

        // Act
        String result = screeningCommand.listScreenings();

        // Assert
        String expected = "Inception (Action, 120 minutes), screened in room Room 1, at 2023-12-05 10:00";
        assertEquals(expected, result);
        verify(screeningService).getScreenings();
    }

    @Test
    void testListScreenings_Failure() {
        // Arrange
        when(screeningService.getScreenings()).thenReturn(Result.failure("There are no screenings"));

        // Act
        String result = screeningCommand.listScreenings();

        // Assert
        assertEquals("There are no screenings", result);
        verify(screeningService).getScreenings();
    }

    @Test
    void testDeleteScreening_Success() {
        // Arrange
        String movieTitle = "Inception";
        String roomName = "Room 1";
        String startTime = "2023-12-05 10:00";
        LocalDateTime parsedStartTime = LocalDateTime.parse(startTime, dateTimeFormatter);

        when(screeningService.deleteScreening(movieTitle, roomName, parsedStartTime))
                .thenReturn(Result.success(null));

        // Act
        String result = screeningCommand.deleteScreening(movieTitle, roomName, startTime);

        // Assert
        assertEquals("Screening deleted", result);
        verify(screeningService).deleteScreening(movieTitle, roomName, parsedStartTime);
    }

    @Test
    void testDeleteScreening_Failure() {
        // Arrange
        String movieTitle = "Inception";
        String roomName = "Room 1";
        String startTime = "2023-12-05 10:00";
        LocalDateTime parsedStartTime = LocalDateTime.parse(startTime, dateTimeFormatter);

        when(screeningService.deleteScreening(movieTitle, roomName, parsedStartTime))
                .thenReturn(Result.failure("Error: Screening does not exist"));

        // Act
        String result = screeningCommand.deleteScreening(movieTitle, roomName, startTime);

        // Assert
        assertEquals("Error: Screening does not exist", result);
        verify(screeningService).deleteScreening(movieTitle, roomName, parsedStartTime);
    }

    @Test
    void testDeleteScreening_InvalidDateFormat() {
        // Arrange
        String movieTitle = "Inception";
        String roomName = "Room 1";
        String startTime = "invalid-date";

        // Act
        String result = screeningCommand.deleteScreening(movieTitle, roomName, startTime);

        // Assert
        assertEquals("Error: Invalid date format. Use 'yyyy-MM-dd HH:mm'.", result);
        verifyNoInteractions(screeningService);
    }
}
