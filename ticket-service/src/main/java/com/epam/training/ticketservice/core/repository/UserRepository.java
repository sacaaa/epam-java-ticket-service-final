package com.epam.training.ticketservice.core.repository;

import com.epam.training.ticketservice.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    Optional<User> findByUsernameAndPassword(String username, String password);

}
