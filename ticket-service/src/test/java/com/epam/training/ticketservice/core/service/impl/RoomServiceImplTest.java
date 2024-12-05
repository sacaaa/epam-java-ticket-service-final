package com.epam.training.ticketservice.core.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.Room;
import com.epam.training.ticketservice.core.model.dto.RoomDto;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RoomServiceImpl roomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRoom_WhenAdminPrivilegesAreValidAndRoomDoesNotExist() {
        // Arrange
        String name = "Room 1";
        int rows = 10;
        int columns = 20;
        int seats = rows * columns;
        Room room = new Room(name, rows, columns);
        RoomDto roomDto = new RoomDto(name, rows, columns, seats, null);

        when(userService.checkAdminPrivileges()).thenReturn(Result.success(null));
        when(roomRepository.findByName(name)).thenReturn(Optional.empty());
        when(roomRepository.save(any(Room.class))).thenReturn(room);
        when(objectMapper.convertValue(room, RoomDto.class)).thenReturn(roomDto);

        // Act
        Result<RoomDto> result = roomService.createRoom(name, rows, columns);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(name, result.getData().getName());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void testCreateRoom_WhenAdminPrivilegesAreInvalid() {
        // Arrange
        String name = "Room 1";
        int rows = 10;
        int columns = 20;

        when(userService.checkAdminPrivileges()).thenReturn(Result.failure("Error: Admin privileges are required."));

        // Act
        Result<RoomDto> result = roomService.createRoom(name, rows, columns);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Admin privileges are required.", result.getMessage());
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void testCreateRoom_WhenRoomAlreadyExists() {
        // Arrange
        String name = "Room 1";
        int rows = 10;
        int columns = 20;

        when(userService.checkAdminPrivileges()).thenReturn(Result.success(null));
        when(roomRepository.findByName(name)).thenReturn(Optional.of(new Room(name, rows, columns)));

        // Act
        Result<RoomDto> result = roomService.createRoom(name, rows, columns);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Room already exists.", result.getMessage());
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void testGetRooms_WhenRoomsExist() {
        // Arrange
        Room room1 = new Room("Room 1", 10, 20);
        Room room2 = new Room("Room 2", 15, 25);
        int seats = room1.getRows() * room1.getColumns();
        int seats2 = room2.getRows() * room2.getColumns();
        RoomDto roomDto1 = new RoomDto("Room 1", 10, 20, seats, null);
        RoomDto roomDto2 = new RoomDto("Room 2", 15, 25, seats2, null);

        when(roomRepository.findAll()).thenReturn(List.of(room1, room2));
        when(objectMapper.convertValue(room1, RoomDto.class)).thenReturn(roomDto1);
        when(objectMapper.convertValue(room2, RoomDto.class)).thenReturn(roomDto2);

        // Act
        Result<List<RoomDto>> result = roomService.getRooms();

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(2, result.getData().size());
    }

    @Test
    void testGetRooms_WhenNoRoomsExist() {
        // Arrange
        when(roomRepository.findAll()).thenReturn(List.of());

        // Act
        Result<List<RoomDto>> result = roomService.getRooms();

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("There are no rooms at the moment", result.getMessage());
    }

    @Test
    void testUpdateRoom_WhenRoomExists() {
        // Arrange
        String name = "Room 1";
        int rows = 15;
        int columns = 25;
        int seats = rows * columns;
        Room room = new Room(name, 10, 20);
        RoomDto updatedRoomDto = new RoomDto(name, rows, columns, seats, null);

        when(userService.checkAdminPrivileges()).thenReturn(Result.success(null));
        when(roomRepository.findByName(name)).thenReturn(Optional.of(room));
        when(objectMapper.convertValue(room, RoomDto.class)).thenReturn(updatedRoomDto);

        // Act
        Result<RoomDto> result = roomService.updateRoom(name, rows, columns);

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(rows, result.getData().getRows());
        assertEquals(columns, result.getData().getColumns());
        assertEquals(seats, result.getData().getSeats());
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void testUpdateRoom_WhenRoomDoesNotExist() {
        // Arrange
        String name = "Room 1";
        int rows = 15;
        int columns = 25;

        when(userService.checkAdminPrivileges()).thenReturn(Result.success(null));
        when(roomRepository.findByName(name)).thenReturn(Optional.empty());

        // Act
        Result<RoomDto> result = roomService.updateRoom(name, rows, columns);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Room does not exist.", result.getMessage());
    }

    @Test
    void testDeleteRoom_WhenRoomExists() {
        // Arrange
        String name = "Room 1";
        Room room = new Room(name, 10, 20);
        int seats = room.getRows() * room.getColumns();
        RoomDto roomDto = new RoomDto(name, 10, 20, seats, null);

        when(userService.checkAdminPrivileges()).thenReturn(Result.success(null));
        when(roomRepository.findByName(name)).thenReturn(Optional.of(room));
        when(objectMapper.convertValue(room, RoomDto.class)).thenReturn(roomDto);

        // Act
        Result<RoomDto> result = roomService.deleteRoom(name);

        // Assert
        assertTrue(result.isSuccess());
        verify(roomRepository, times(1)).delete(room);
    }

    @Test
    void testDeleteRoom_WhenRoomDoesNotExist() {
        // Arrange
        String name = "Room 1";

        when(userService.checkAdminPrivileges()).thenReturn(Result.success(null));
        when(roomRepository.findByName(name)).thenReturn(Optional.empty());

        // Act
        Result<RoomDto> result = roomService.deleteRoom(name);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Room does not exist.", result.getMessage());
        verify(roomRepository, never()).delete(any(Room.class));
    }

    @Test
    void testUpdateRoom_WhenAdminPrivilegesAreInvalid() {
        // Arrange
        String name = "Room 1";
        int rows = 15;
        int columns = 25;

        when(userService.checkAdminPrivileges()).thenReturn(Result.failure("Error: Admin privileges are required."));

        // Act
        Result<RoomDto> result = roomService.updateRoom(name, rows, columns);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Admin privileges are required.", result.getMessage());
        verifyNoInteractions(roomRepository);
    }

    @Test
    void testDeleteRoom_WhenAdminPrivilegesAreInvalid() {
        // Arrange
        String name = "Room 1";

        when(userService.checkAdminPrivileges()).thenReturn(Result.failure("Error: Admin privileges are required."));

        // Act
        Result<RoomDto> result = roomService.deleteRoom(name);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Admin privileges are required.", result.getMessage());
        verifyNoInteractions(roomRepository);
    }

}
