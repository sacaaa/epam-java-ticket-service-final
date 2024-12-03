package com.epam.training.ticketservice.core.repository;

import com.epam.training.ticketservice.core.model.Room;
import com.epam.training.ticketservice.core.model.Screening;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    List<Screening> findByRoom(Room room);

    Optional<Screening> findByMovieTitleAndRoomNameAndStartTime(String movieTitle,
                                                                String roomName,
                                                                LocalDateTime startTime);

}
