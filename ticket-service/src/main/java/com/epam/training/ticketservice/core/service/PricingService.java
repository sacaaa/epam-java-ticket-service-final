package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.data.Result;

import java.time.LocalDateTime;
import java.util.List;

public interface PricingService {

    Result<Void> createPricingComponent(String name, int amount);

    Result<Void> updateBasePrice(int newBasePrice);

    Result<Void> attachPricingComponentToMovie(String pricingComponentName, String movieTitle);

    Result<Void> attachPricingComponentToRoom(String pricingComponentName, String roomName);

    Result<Void> attachPricingComponentToScreening(String pricingComponentName,
                                                   String movieTitle,
                                                   String roomName,
                                                   LocalDateTime startTime);

    Result<Integer> calculatePrice(String movieTitle, String roomName, LocalDateTime startDateTime, List<String> seats);

}
