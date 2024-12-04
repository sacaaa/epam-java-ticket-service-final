package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.service.impl.PricingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Optional;

@ShellComponent
@RequiredArgsConstructor
public class PricingCommand {

    private final PricingServiceImpl pricingService;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @ShellMethod(key = "create price component", value = "Create a new pricing component")
    public String createPriceComponent(String componentName, int price) {
        var result = pricingService.createPricingComponent(componentName, price);
        return result.isSuccess() ? "Price component created" : result.getMessage();
    }

    @ShellMethod(key = "attach price component to movie", value = "Attach a pricing component to a movie")
    public String attachPriceComponentToMovie(String componentName, String movieTitle) {
        var result = pricingService.attachPricingComponentToMovie(componentName, movieTitle);
        return result.isSuccess() ? "Price component attached to movie" : result.getMessage();
    }

    @ShellMethod(key = "attach price component to room", value = "Attach a pricing component to a room")
    public String attachPriceComponentToRoom(String componentName, String roomName) {
        var result = pricingService.attachPricingComponentToRoom(componentName, roomName);
        return result.isSuccess() ? "Price component attached to room" : result.getMessage();
    }

    @ShellMethod(key = "attach price component to screening", value = "Attach a pricing component to a screening")
    public String attachPriceComponentToScreening(String componentName,
                                                  String movieTitle,
                                                  String roomName,
                                                  String startTime) {
        return parseDateTime(startTime)
                .map(startDateTime -> pricingService
                        .attachPricingComponentToScreening(componentName, movieTitle, roomName, startDateTime))
                .map(result -> result.isSuccess() ? "Price component attached to screening" : result.getMessage())
                .orElse("Error: Invalid date format. Use 'yyyy-MM-dd HH:mm'.");
    }

    @ShellMethod(key = "update base price", value = "Update the default base price")
    public String updateBasePrice(int newBasePrice) {
        var result = pricingService.updateBasePrice(newBasePrice);
        return result.isSuccess() ? "Base price updated" : result.getMessage();
    }

    @ShellMethod(key = "show price for", value = "Show the price for a specific screening and seat")
    public String showPriceFor(String movieTitle, String roomName, String startTime, String seat) {
        return parseDateTime(startTime)
                .map(startDateTime -> pricingService
                        .calculatePrice(movieTitle, roomName, startDateTime, Collections.singletonList(seat)))
                .map(result -> result.isSuccess()
                        ? String.format("The price for this booking would be %d HUF", result.getData())
                        : result.getMessage())
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
