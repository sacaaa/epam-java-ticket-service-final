package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.service.impl.BookingServiceImpl;
import com.epam.training.ticketservice.core.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class BookingCommand {

    private final BookingServiceImpl bookingService;

    private final UserServiceImpl userService;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @ShellMethod(key = "book", value = "Book tickets for a screening")
    public String bookTickets(String movieTitle, String roomName, String startTime, String seats) {
        return parseDateTime(startTime)
                .map(startDateTime -> {
                    List<String> seatList = parseSeats(seats);
                    var result = bookingService.createBooking(
                            getLoggedInUsername(), movieTitle, roomName, startDateTime, seatList);
                    return result.isSuccess()
                            ? String.format("Seats booked: %s; the price for this booking is %d HUF",
                            seatList.stream().map(seat -> "(" + seat + ")").collect(Collectors.joining(", ")),
                            result.getData().getPrice())
                            : result.getMessage();
                })
                .orElse("Error: Invalid date format. Use 'yyyy-MM-dd HH:mm'.");
    }

    @ShellMethod(key = "list bookings", value = "List all bookings for the current user")
    public String listBookings() {
        var result = bookingService.getBookingsByUser(getLoggedInUsername());

        return result.isSuccess()
                ? result.getData().stream()
                .map(booking -> String.format(
                        "Seats %s on %s in room %s starting at %s for %d HUF",
                        booking.getSeats().stream().map(seat -> "(" + seat + ")").collect(Collectors.joining(", ")),
                        booking.getScreening().getMovie().getTitle(),
                        booking.getScreening().getRoom().getName(),
                        booking.getScreening().getStartTime().format(dateTimeFormatter),
                        booking.getPrice()))
                .collect(Collectors.joining("\n"))
                : result.getMessage();
    }

    private Optional<LocalDateTime> parseDateTime(String startTime) {
        try {
            return Optional.of(LocalDateTime.parse(startTime, dateTimeFormatter));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    private List<String> parseSeats(String seats) {
        return Arrays.stream(seats.split(" ")).collect(Collectors.toList());
    }

    private String getLoggedInUsername() {
        return userService.getLoggedInUser().getUsername();
    }

}
