package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.data.Result;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing pricing components and calculating ticket prices.
 */
public interface PricingService {

    /**
     * Creates a new pricing component with the specified name and amount.
     *
     * @param name   The unique name of the pricing component.
     * @param amount The amount associated with the pricing component.
     * @return A {@code Result} indicating success or failure of the operation.
     */
    Result<Void> createPricingComponent(String name, int amount);

    /**
     * Updates the base price for all tickets.
     *
     * @param newBasePrice The new base price for tickets.
     * @return A {@code Result} indicating success or failure of the operation.
     */
    Result<Void> updateBasePrice(int newBasePrice);

    /**
     * Attaches a pricing component to a specific movie.
     *
     * @param pricingComponentName The name of the pricing component to attach.
     * @param movieTitle           The title of the movie to attach the component to.
     * @return A {@code Result} indicating success or failure of the operation.
     */
    Result<Void> attachPricingComponentToMovie(String pricingComponentName, String movieTitle);

    /**
     * Attaches a pricing component to a specific room.
     *
     * @param pricingComponentName The name of the pricing component to attach.
     * @param roomName             The name of the room to attach the component to.
     * @return A {@code Result} indicating success or failure of the operation.
     */
    Result<Void> attachPricingComponentToRoom(String pricingComponentName, String roomName);

    /**
     * Attaches a pricing component to a specific screening.
     *
     * @param pricingComponentName The name of the pricing component to attach.
     * @param movieTitle           The title of the movie being screened.
     * @param roomName             The name of the room where the screening takes place.
     * @param startTime            The start time of the screening.
     * @return A {@code Result} indicating success or failure of the operation.
     */
    Result<Void> attachPricingComponentToScreening(String pricingComponentName,
                                                   String movieTitle,
                                                   String roomName,
                                                   LocalDateTime startTime);

    /**
     * Calculates the total ticket price for a given screening and list of seats.
     *
     * @param movieTitle     The title of the movie being screened.
     * @param roomName       The name of the room where the screening takes place.
     * @param startDateTime  The start time of the screening.
     * @param seats          The list of seats for which the price is to be calculated.
     * @return A {@code Result} containing the total price or an error message if the calculation fails.
     */
    Result<Integer> calculatePrice(String movieTitle, String roomName, LocalDateTime startDateTime, List<String> seats);

}
