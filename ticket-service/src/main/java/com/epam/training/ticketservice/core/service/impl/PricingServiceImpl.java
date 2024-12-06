package com.epam.training.ticketservice.core.service.impl;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.Pricing;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import com.epam.training.ticketservice.core.repository.ScreeningRepository;
import com.epam.training.ticketservice.core.repository.PricingRepository;
import com.epam.training.ticketservice.core.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    private final PricingRepository pricingRepository;

    private final MovieRepository movieRepository;

    private final RoomRepository roomRepository;

    private final ScreeningRepository screeningRepository;

    /**
     * The base price for a ticket, which can be adjusted.
     */
    private static int basePrice = 1500;

    @Override
    public Result<Void> createPricingComponent(String name, int amount) {
        if (pricingRepository.findByName(name).isPresent()) {
            return Result.failure("Error: Pricing component already exists.");
        }
        pricingRepository.save(new Pricing(name, amount));
        return Result.success(null);
    }

    @Override
    public Result<Void> updateBasePrice(int newBasePrice) {
        basePrice = newBasePrice;
        return Result.success(null);
    }

    @Override
    public Result<Void> attachPricingComponentToMovie(String pricingComponentName, String movieTitle) {
        var pricing = pricingRepository.findByName(pricingComponentName)
                .orElse(null);
        if (pricing == null) {
            return Result.failure("Error: Pricing component does not exist.");
        }

        var movie = movieRepository.findByTitle(movieTitle)
                .orElse(null);
        if (movie == null) {
            return Result.failure("Error: Movie does not exist.");
        }

        movie.getPricingComponents().add(pricing);
        movieRepository.save(movie);

        return Result.success(null);
    }

    @Override
    public Result<Void> attachPricingComponentToRoom(String pricingComponentName, String roomName) {
        var pricing = pricingRepository.findByName(pricingComponentName)
                .orElse(null);
        if (pricing == null) {
            return Result.failure("Error: Pricing component does not exist.");
        }

        var room = roomRepository.findByName(roomName)
                .orElse(null);
        if (room == null) {
            return Result.failure("Error: Room does not exist.");
        }

        room.getPricingComponents().add(pricing);
        roomRepository.save(room);

        return Result.success(null);
    }

    @Override
    public Result<Void> attachPricingComponentToScreening(String pricingComponentName,
                                                          String movieTitle,
                                                          String roomName,
                                                          LocalDateTime startTime) {
        var pricing = pricingRepository.findByName(pricingComponentName)
                .orElse(null);
        if (pricing == null) {
            return Result.failure("Error: Pricing component does not exist.");
        }

        var screening = screeningRepository.findByMovieTitleAndRoomNameAndStartTime(movieTitle, roomName, startTime)
                .orElse(null);
        if (screening == null) {
            return Result.failure("Error: Screening does not exist.");
        }

        screening.getPricingComponents().add(pricing);
        screeningRepository.save(screening);

        return Result.success(null);
    }

    @Override
    public Result<Integer> calculatePrice(String movieTitle,
                                          String roomName,
                                          LocalDateTime startDateTime,
                                          List<String> seats) {
        var screening = screeningRepository.findByMovieTitleAndRoomNameAndStartTime(movieTitle, roomName, startDateTime)
                .orElse(null);
        if (screening == null) {
            return Result.failure("Error: Screening does not exist.");
        }

        int totalPrice = basePrice
                + calculatePricingComponents(screening.getMovie().getPricingComponents())
                + calculatePricingComponents(screening.getRoom().getPricingComponents())
                + calculatePricingComponents(screening.getPricingComponents());

        totalPrice *= seats.size();

        return Result.success(totalPrice);
    }

    /**
     * Calculates the total additional pricing from a collection of pricing components.
     *
     * @param pricingComponents The collection of pricing components to calculate.
     * @return The total additional price contributed by the pricing components.
     */
    private int calculatePricingComponents(Collection<Pricing> pricingComponents) {
        return pricingComponents.stream().mapToInt(Pricing::getAmount).sum();
    }

}
