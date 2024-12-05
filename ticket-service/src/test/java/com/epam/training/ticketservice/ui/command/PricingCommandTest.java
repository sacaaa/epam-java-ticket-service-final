package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.service.impl.PricingServiceImpl;
import com.epam.training.ticketservice.core.data.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PricingCommandTest {

    @Mock
    private PricingServiceImpl pricingService;

    @InjectMocks
    private PricingCommand pricingCommand;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePriceComponent_Success() {
        // Arrange
        String componentName = "Premium";
        int price = 500;
        when(pricingService.createPricingComponent(componentName, price)).thenReturn(Result.success(null));

        // Act
        String result = pricingCommand.createPriceComponent(componentName, price);

        // Assert
        assertEquals("Price component created", result);
        verify(pricingService).createPricingComponent(componentName, price);
    }

    @Test
    void testCreatePriceComponent_Failure() {
        // Arrange
        String componentName = "Premium";
        int price = 500;
        when(pricingService.createPricingComponent(componentName, price))
                .thenReturn(Result.failure("Error: Component already exists."));

        // Act
        String result = pricingCommand.createPriceComponent(componentName, price);

        // Assert
        assertEquals("Error: Component already exists.", result);
        verify(pricingService).createPricingComponent(componentName, price);
    }

    @Test
    void testAttachPriceComponentToMovie_Success() {
        // Arrange
        String componentName = "Premium";
        String movieTitle = "Inception";
        when(pricingService.attachPricingComponentToMovie(componentName, movieTitle)).thenReturn(Result.success(null));

        // Act
        String result = pricingCommand.attachPriceComponentToMovie(componentName, movieTitle);

        // Assert
        assertEquals("Price component attached to movie", result);
        verify(pricingService).attachPricingComponentToMovie(componentName, movieTitle);
    }

    @Test
    void testAttachPriceComponentToMovie_Failure() {
        // Arrange
        String componentName = "Premium";
        String movieTitle = "Inception";
        when(pricingService.attachPricingComponentToMovie(componentName, movieTitle))
                .thenReturn(Result.failure("Error: Movie does not exist."));

        // Act
        String result = pricingCommand.attachPriceComponentToMovie(componentName, movieTitle);

        // Assert
        assertEquals("Error: Movie does not exist.", result);
        verify(pricingService).attachPricingComponentToMovie(componentName, movieTitle);
    }

    @Test
    void testAttachPriceComponentToRoom_Success() {
        // Arrange
        String componentName = "Premium";
        String roomName = "Room 1";
        when(pricingService.attachPricingComponentToRoom(componentName, roomName)).thenReturn(Result.success(null));

        // Act
        String result = pricingCommand.attachPriceComponentToRoom(componentName, roomName);

        // Assert
        assertEquals("Price component attached to room", result);
        verify(pricingService).attachPricingComponentToRoom(componentName, roomName);
    }

    @Test
    void testAttachPriceComponentToRoom_Failure() {
        // Arrange
        String componentName = "Premium";
        String roomName = "Room 1";
        when(pricingService.attachPricingComponentToRoom(componentName, roomName))
                .thenReturn(Result.failure("Error: Room does not exist."));

        // Act
        String result = pricingCommand.attachPriceComponentToRoom(componentName, roomName);

        // Assert
        assertEquals("Error: Room does not exist.", result);
        verify(pricingService).attachPricingComponentToRoom(componentName, roomName);
    }

    @Test
    void testAttachPriceComponentToScreening_Success() {
        // Arrange
        String componentName = "Premium";
        String movieTitle = "Inception";
        String roomName = "Room 1";
        String startTime = "2023-12-06 14:00";
        LocalDateTime parsedStartTime = LocalDateTime.parse(startTime, dateTimeFormatter);

        when(pricingService.attachPricingComponentToScreening(componentName, movieTitle, roomName, parsedStartTime))
                .thenReturn(Result.success(null));

        // Act
        String result = pricingCommand.attachPriceComponentToScreening(componentName, movieTitle, roomName, startTime);

        // Assert
        assertEquals("Price component attached to screening", result);
        verify(pricingService).attachPricingComponentToScreening(componentName, movieTitle, roomName, parsedStartTime);
    }

    @Test
    void testAttachPriceComponentToScreening_Failure_InvalidDate() {
        // Arrange
        String componentName = "Premium";
        String movieTitle = "Inception";
        String roomName = "Room 1";
        String invalidStartTime = "invalid-date";

        // Act
        String result = pricingCommand.attachPriceComponentToScreening(componentName, movieTitle, roomName, invalidStartTime);

        // Assert
        assertEquals("Error: Invalid date format. Use 'yyyy-MM-dd HH:mm'.", result);
        verify(pricingService, never()).attachPricingComponentToScreening(anyString(), anyString(), anyString(), any());
    }

    @Test
    void testUpdateBasePrice_Success() {
        // Arrange
        int newBasePrice = 2000;
        when(pricingService.updateBasePrice(newBasePrice)).thenReturn(Result.success(null));

        // Act
        String result = pricingCommand.updateBasePrice(newBasePrice);

        // Assert
        assertEquals("Base price updated", result);
        verify(pricingService).updateBasePrice(newBasePrice);
    }

    @Test
    void testShowPriceFor_Success() {
        // Arrange
        String movieTitle = "Inception";
        String roomName = "Room 1";
        String startTime = "2023-12-06 14:00";
        String seat = "A1";
        LocalDateTime parsedStartTime = LocalDateTime.parse(startTime, dateTimeFormatter);

        when(pricingService.calculatePrice(movieTitle, roomName, parsedStartTime, Collections.singletonList(seat)))
                .thenReturn(Result.success(2500));

        // Act
        String result = pricingCommand.showPriceFor(movieTitle, roomName, startTime, seat);

        // Assert
        assertEquals("The price for this booking would be 2500 HUF", result);
        verify(pricingService).calculatePrice(movieTitle, roomName, parsedStartTime, Collections.singletonList(seat));
    }

    @Test
    void testShowPriceFor_Failure_InvalidDate() {
        // Arrange
        String movieTitle = "Inception";
        String roomName = "Room 1";
        String invalidStartTime = "invalid-date";
        String seat = "A1";

        // Act
        String result = pricingCommand.showPriceFor(movieTitle, roomName, invalidStartTime, seat);

        // Assert
        assertEquals("Error: Invalid date format. Use 'yyyy-MM-dd HH:mm'.", result);
        verify(pricingService, never()).calculatePrice(anyString(), anyString(), any(), anyList());
    }

}
