package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.service.impl.ScreeningServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class ScreeningCommand {

    private final ScreeningServiceImpl screeningService;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @ShellMethod(key = "create screening", value = "Create a new screening")
    public String createScreening(String movieTitle, String roomName, String startTime) {
        return parseDateTime(startTime)
                .map(startDateTime -> screeningService.createScreening(movieTitle, roomName, startDateTime))
                .map(result -> result.isSuccess() ? "Screening created" : result.getMessage())
                .orElse("Error: Invalid date format. Use 'yyyy-MM-dd HH:mm'.");
    }

    @ShellMethod(key = "list screenings", value = "List all screenings")
    public String listScreenings() {
        var result = screeningService.getScreenings();

        return result.isSuccess()
                ? result.getData().stream()
                .map(screening -> String.format("%s (%s, %d minutes), screened in room %s, at %s",
                        screening.getMovie().getTitle(),
                        screening.getMovie().getGenre(),
                        screening.getMovie().getLength(),
                        screening.getRoom().getName(),
                        screening.getStartTime().format(dateTimeFormatter)))
                .collect(Collectors.joining("\n"))
                : result.getMessage();
    }

    @ShellMethod(key = "delete screening", value = "Delete an existing screening")
    public String deleteScreening(String movieTitle, String roomName, String startTime) {
        return parseDateTime(startTime)
                .map(startDateTime -> screeningService.deleteScreening(movieTitle, roomName, startDateTime))
                .map(result -> result.isSuccess() ? "Screening deleted" : result.getMessage())
                .orElse("Error: Invalid date format. Use 'yyyy-MM-dd HH:mm'.");
    }

    private Optional<LocalDateTime> parseDateTime(String startTime) {
        try {
            return Optional.of(LocalDateTime.parse(startTime, dateTimeFormatter));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

}
