package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.service.impl.RoomServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class RoomCommand {

    private final RoomServiceImpl roomService;

    @ShellMethod(key = "create room", value = "Create a new room")
    public String createRoom(String name, int rows, int columns) {
        var result = roomService.createRoom(name, rows, columns);
        return result.isSuccess() ? "Room created" : result.getMessage();
    }

    @ShellMethod(key = "update room", value = "Update an existing room")
    public String updateRoom(String name, int rows, int columns) {
        var result = roomService.updateRoom(name, rows, columns);
        return result.isSuccess() ? "Room updated" : result.getMessage();
    }

    @ShellMethod(key = "list rooms", value = "List all rooms")
    public String listRooms() {
        var result = roomService.getRooms();

        return result.isSuccess()
                ? result.getData().stream()
                .map(room -> String.format(
                        "Room %s with %d seats, %d rows and %d columns",
                        room.getName(), room.getSeats(), room.getRows(), room.getColumns()))
                .collect(Collectors.joining("\n"))
                : result.getMessage();
    }

    @ShellMethod(key = "delete room", value = "Delete an existing room")
    public String deleteRoom(String name) {
        var result = roomService.deleteRoom(name);
        return result.isSuccess() ? "Room deleted" : result.getMessage();
    }

}
