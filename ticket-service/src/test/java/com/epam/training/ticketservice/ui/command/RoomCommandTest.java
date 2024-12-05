package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.service.impl.RoomServiceImpl;
import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.dto.RoomDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RoomCommandTest {

    @Mock
    private RoomServiceImpl roomService;

    @InjectMocks
    private RoomCommand roomCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRoom_Success() {
        // Arrange
        String name = "Room 1";
        int rows = 10;
        int columns = 15;

        when(roomService.createRoom(name, rows, columns)).thenReturn(Result.success(null));

        // Act
        String result = roomCommand.createRoom(name, rows, columns);

        // Assert
        assertEquals("Room created", result);
        verify(roomService).createRoom(name, rows, columns);
    }

    @Test
    void testCreateRoom_Failure() {
        // Arrange
        String name = "Room 1";
        int rows = 10;
        int columns = 15;

        when(roomService.createRoom(name, rows, columns))
                .thenReturn(Result.failure("Error: Room already exists."));

        // Act
        String result = roomCommand.createRoom(name, rows, columns);

        // Assert
        assertEquals("Error: Room already exists.", result);
        verify(roomService).createRoom(name, rows, columns);
    }

    @Test
    void testUpdateRoom_Success() {
        // Arrange
        String name = "Room 1";
        int rows = 12;
        int columns = 20;

        when(roomService.updateRoom(name, rows, columns)).thenReturn(Result.success(null));

        // Act
        String result = roomCommand.updateRoom(name, rows, columns);

        // Assert
        assertEquals("Room updated", result);
        verify(roomService).updateRoom(name, rows, columns);
    }

    @Test
    void testUpdateRoom_Failure() {
        // Arrange
        String name = "Room 1";
        int rows = 12;
        int columns = 20;

        when(roomService.updateRoom(name, rows, columns))
                .thenReturn(Result.failure("Error: Room does not exist."));

        // Act
        String result = roomCommand.updateRoom(name, rows, columns);

        // Assert
        assertEquals("Error: Room does not exist.", result);
        verify(roomService).updateRoom(name, rows, columns);
    }

    @Test
    void testListRooms_Success() {
        // Arrange
        RoomDto room1 = new RoomDto("Room 1", 10, 15, 150, null);
        RoomDto room2 = new RoomDto("Room 2", 12, 20, 240, null);

        when(roomService.getRooms()).thenReturn(Result.success(List.of(room1, room2)));

        // Act
        String result = roomCommand.listRooms();

        // Assert
        String expected = "Room Room 1 with 150 seats, 10 rows and 15 columns\n"
                + "Room Room 2 with 240 seats, 12 rows and 20 columns";
        assertEquals(expected, result);
        verify(roomService).getRooms();
    }

    @Test
    void testListRooms_Failure() {
        // Arrange
        when(roomService.getRooms()).thenReturn(Result.failure("There are no rooms at the moment"));

        // Act
        String result = roomCommand.listRooms();

        // Assert
        assertEquals("There are no rooms at the moment", result);
        verify(roomService).getRooms();
    }

    @Test
    void testDeleteRoom_Success() {
        // Arrange
        String name = "Room 1";

        when(roomService.deleteRoom(name)).thenReturn(Result.success(null));

        // Act
        String result = roomCommand.deleteRoom(name);

        // Assert
        assertEquals("Room deleted", result);
        verify(roomService).deleteRoom(name);
    }

    @Test
    void testDeleteRoom_Failure() {
        // Arrange
        String name = "Room 1";

        when(roomService.deleteRoom(name)).thenReturn(Result.failure("Error: Room does not exist."));

        // Act
        String result = roomCommand.deleteRoom(name);

        // Assert
        assertEquals("Error: Room does not exist.", result);
        verify(roomService).deleteRoom(name);
    }
}
