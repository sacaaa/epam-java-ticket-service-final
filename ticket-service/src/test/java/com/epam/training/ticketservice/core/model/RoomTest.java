package com.epam.training.ticketservice.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @Test
    void testCalculateSeats_WhenRowsAndColumnsAreSet() {
        // Arrange
        Room room = new Room();
        room.setRows(10);
        room.setColumns(20);

        // Act
        room.calculateSeats();

        // Assert
        assertEquals(200, room.getSeats());
    }

    @Test
    void testCalculateSeats_WhenRowsOrColumnsAreZero() {
        // Arrange
        Room room = new Room();
        room.setRows(0);
        room.setColumns(20);

        // Act
        room.calculateSeats();

        // Assert
        assertEquals(0, room.getSeats());
    }

    @Test
    void testCalculateSeats_WhenNegativeValuesAreSet() {
        // Arrange
        Room room = new Room();
        room.setRows(-10);
        room.setColumns(20);

        // Act
        room.calculateSeats();

        // Assert
        assertEquals(-200, room.getSeats());
    }
}
