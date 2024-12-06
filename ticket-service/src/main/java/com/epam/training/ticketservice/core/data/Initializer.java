package com.epam.training.ticketservice.core.data;

import com.epam.training.ticketservice.core.model.User;
import com.epam.training.ticketservice.core.model.enums.Role;
import com.epam.training.ticketservice.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Initializes the system with default data.
 */
@Component
@RequiredArgsConstructor
public class Initializer {

    private final UserRepository userRepository;

    /**
     * Initializes the system by checking if an admin user exists.
     * If no admin user is found, it creates one with default credentials:
     * username: "admin", password: "admin", role: {@code Role.ADMIN}.
     */
    @PostConstruct
    public void init() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", "admin", Role.ADMIN);
            userRepository.save(admin);
        }
    }

}
