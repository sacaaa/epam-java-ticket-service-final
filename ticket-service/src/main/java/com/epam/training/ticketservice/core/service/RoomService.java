package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.dto.RoomDto;

import java.util.List;
import java.util.Optional;

public interface RoomService {

    Result<RoomDto> createRoom(String name, int rows, int columns);

    Result<List<RoomDto>> getRooms();

    Result<RoomDto> updateRoom(String name, int rows, int columns);

    Result<RoomDto> deleteRoom(String name);

}
