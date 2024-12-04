package com.epam.training.ticketservice.core.service.impl;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.Movie;
import com.epam.training.ticketservice.core.model.Room;
import com.epam.training.ticketservice.core.model.Screening;
import com.epam.training.ticketservice.core.model.dto.ScreeningDto;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import com.epam.training.ticketservice.core.repository.ScreeningRepository;
import com.epam.training.ticketservice.core.service.ScreeningService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreeningServiceImpl implements ScreeningService {

    private final ScreeningRepository screeningRepository;

    private final MovieRepository movieRepository;

    private final RoomRepository roomRepository;

    private final ObjectMapper objectMapper;

    private final UserServiceImpl userService;

    @Override
    public Result<ScreeningDto> createScreening(String movieTitle, String roomName, LocalDateTime startTime) {
        Result<Void> adminCheck = userService.checkAdminPrivileges();
        if (!adminCheck.isSuccess()) {
            return Result.failure(adminCheck.getMessage());
        }

        Optional<Movie> movie = movieRepository.findByTitle(movieTitle);
        if (movie.isEmpty()) {
            return Result.failure("Error: Movie does not exist.");
        }

        Optional<Room> room = roomRepository.findByName(roomName);
        if (room.isEmpty()) {
            return Result.failure("Error: Room does not exist.");
        }

        Result<Void> scheduleCheck = checkScheduleConflicts(room.get(), startTime);
        if (!scheduleCheck.isSuccess()) {
            return Result.failure(scheduleCheck.getMessage());
        }

        Screening screening = new Screening(movie.get(), room.get(), startTime);
        screeningRepository.save(screening);

        return Result.success(objectMapper.convertValue(screening, ScreeningDto.class));
    }

    private Result<Void> checkScheduleConflicts(Room room, LocalDateTime startTime) {
        List<Screening> existingScreenings = screeningRepository.findByRoom(room);
        for (Screening screening : existingScreenings) {
            LocalDateTime existingStart = screening.getStartTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(screening.getMovie().getLength());
            LocalDateTime breakEnd = existingEnd.plusMinutes(10);

            if (isOverlapping(startTime, existingStart, existingEnd)) {
                return Result.failure("There is an overlapping screening");
            }

            if (isInBreakPeriod(startTime, existingEnd, breakEnd)) {
                return Result.failure("This would start in the break period after another screening in this room");
            }
        }
        return Result.success(null);
    }

    private boolean isOverlapping(LocalDateTime startTime, LocalDateTime existingStart, LocalDateTime existingEnd) {
        return !startTime.isBefore(existingStart) && startTime.isBefore(existingEnd);
    }

    private boolean isInBreakPeriod(LocalDateTime startTime, LocalDateTime existingEnd, LocalDateTime breakEnd) {
        return !startTime.isBefore(existingEnd) && startTime.isBefore(breakEnd);
    }

    @Override
    public Result<List<ScreeningDto>> getScreenings() {
        List<Screening> screenings = screeningRepository.findAll();

        if (screenings.isEmpty()) {
            return Result.failure("There are no screenings");
        }

        List<ScreeningDto> screeningDtos = screenings.stream()
                .map(screening -> objectMapper.convertValue(screening, ScreeningDto.class))
                .collect(Collectors.toList());

        return Result.success(screeningDtos);
    }


    @Override
    public Result<ScreeningDto> deleteScreening(String movieTitle, String roomName, LocalDateTime startTime) {
        Result<Void> adminCheck = userService.checkAdminPrivileges();
        if (!adminCheck.isSuccess()) {
            return Result.failure(adminCheck.getMessage());
        }

        return screeningRepository
                .findByMovieTitleAndRoomNameAndStartTime(movieTitle, roomName, startTime)
                .map(screening -> {
                    screeningRepository.delete(screening);
                    return Result.success(objectMapper.convertValue(screening, ScreeningDto.class));
                })
                .orElseGet(() -> Result.failure("Error: Screening does not exist."));
    }

}
