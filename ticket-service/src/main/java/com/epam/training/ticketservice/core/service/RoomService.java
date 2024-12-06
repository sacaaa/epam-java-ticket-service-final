package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.dto.RoomDto;

import java.util.List;

/**
 * Service interface for managing cinema rooms.
 */
public interface RoomService {

    /**
     * Creates a new room with the specified name, rows, and columns.
     *
     * @param name   The unique name of the room.
     * @param rows   The number of rows in the room.
     * @param columns The number of columns in the room.
     * @return A {@code Result} containing the created {@code RoomDto} or an error message if creation fails.
     */
    Result<RoomDto> createRoom(String name, int rows, int columns);

    /**
     * Retrieves all existing rooms.
     *
     * @return A {@code Result} containing a list of {@code RoomDto} objects representing all rooms,
     *         or an error message if retrieval fails.
     */
    Result<List<RoomDto>> getRooms();

    /**
     * Updates the details of an existing room.
     *
     * @param name   The unique name of the room to update.
     * @param rows   The new number of rows for the room.
     * @param columns The new number of columns for the room.
     * @return A {@code Result} containing the updated {@code RoomDto} or an error message if the update fails.
     */
    Result<RoomDto> updateRoom(String name, int rows, int columns);

    /**
     * Deletes a room with the specified name.
     *
     * @param name The unique name of the room to delete.
     * @return A {@code Result} containing the deleted {@code RoomDto} or an error message if deletion fails.
     */
    Result<RoomDto> deleteRoom(String name);

}