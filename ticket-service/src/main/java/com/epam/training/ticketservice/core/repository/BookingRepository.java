package com.epam.training.ticketservice.core.repository;

import com.epam.training.ticketservice.core.model.Booking;
import com.epam.training.ticketservice.core.model.Screening;
import com.epam.training.ticketservice.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByScreeningAndSeatsContaining(Screening screening, String seat);

    List<Booking> findAllByUser(User user);

}
