package com.epam.training.ticketservice.core.service.impl;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.Room;
import com.epam.training.ticketservice.core.model.dto.RoomDto;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import com.epam.training.ticketservice.core.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    private final ObjectMapper objectMapper;

    private final AuthServiceImpl authService;

    @Override
    public Result<RoomDto> createRoom(String name, int rows, int columns) {
        Result<Void> adminCheck = authService.checkAdminPrivileges();
        if (!adminCheck.isSuccess()) {
            return Result.failure(adminCheck.getMessage());
        }

        if (roomRepository.findByName(name).isPresent()) {
            return Result.failure("Error: Room already exists.");
        }

        Room room = roomRepository.save(new Room(name, rows, columns));
        return Result.success(objectMapper.convertValue(room, RoomDto.class));
    }

    @Override
    public Result<List<RoomDto>> getRooms() {
        List<RoomDto> rooms = roomRepository.findAll().stream()
                .map(room -> objectMapper.convertValue(room, RoomDto.class))
                .toList();

        return rooms.isEmpty()
                ? Result.failure("There are no rooms at the moment")
                : Result.success(rooms);
    }

    @Override
    public Result<RoomDto> updateRoom(String name, int rows, int columns) {
        Result<Void> adminCheck = authService.checkAdminPrivileges();
        if (!adminCheck.isSuccess()) {
            return Result.failure(adminCheck.getMessage());
        }

        return roomRepository.findByName(name)
                .map(room -> {
                    room.setRows(rows);
                    room.setColumns(columns);
                    roomRepository.save(room);
                    return Result.success(objectMapper.convertValue(room, RoomDto.class));
                })
                .orElseGet(() -> Result.failure("Error: Room does not exist."));
    }

    @Override
    public Result<RoomDto> deleteRoom(String name) {
        Result<Void> adminCheck = authService.checkAdminPrivileges();
        if (!adminCheck.isSuccess()) {
            return Result.failure(adminCheck.getMessage());
        }

        return roomRepository.findByName(name)
                .map(room -> {
                    roomRepository.delete(room);
                    return Result.success(objectMapper.convertValue(room, RoomDto.class));
                })
                .orElseGet(() -> Result.failure("Error: Room does not exist."));
    }

}
