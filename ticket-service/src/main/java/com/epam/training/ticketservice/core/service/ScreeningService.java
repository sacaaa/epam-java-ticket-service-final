package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.dto.ScreeningDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ScreeningService {

    Result<ScreeningDto> createScreening(String movieTitle, String roomName, LocalDateTime startTime);

    Result<List<ScreeningDto>> getScreenings();

    Result<ScreeningDto> deleteScreening(String movieTitle, String roomName, LocalDateTime startTime);

}
