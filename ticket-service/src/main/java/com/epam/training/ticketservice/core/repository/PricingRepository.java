package com.epam.training.ticketservice.core.repository;

import com.epam.training.ticketservice.core.model.Pricing;
import com.epam.training.ticketservice.core.model.dto.PricingDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, Long> {

    Optional<Pricing> findByName(String name);

}
