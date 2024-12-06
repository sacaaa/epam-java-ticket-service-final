package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.dto.ScreeningDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing movie screenings.
 */
public interface ScreeningService {

    /**
     * Creates a new screening for a specific movie in a specific room at a specified time.
     *
     * @param movieTitle The title of the movie to be screened.
     * @param roomName   The name of the room where the screening will take place.
     * @param startTime  The start time of the screening.
     * @return A {@code Result} containing the created {@code ScreeningDto} or an error message if creation fails.
     */
    Result<ScreeningDto> createScreening(String movieTitle, String roomName, LocalDateTime startTime);

    /**
     * Retrieves all scheduled screenings.
     *
     * @return A {@code Result} containing a list of {@code ScreeningDto} objects representing all screenings,
     *         or an error message if retrieval fails.
     */
    Result<List<ScreeningDto>> getScreenings();

    /**
     * Deletes a screening for a specific movie in a specific room at a specified time.
     *
     * @param movieTitle The title of the movie to be removed from the schedule.
     * @param roomName   The name of the room where the screening is scheduled.
     * @param startTime  The start time of the screening to be deleted.
     * @return A {@code Result} containing the deleted {@code ScreeningDto} or an error message if deletion fails.
     */
    Result<ScreeningDto> deleteScreening(String movieTitle, String roomName, LocalDateTime startTime);

}
