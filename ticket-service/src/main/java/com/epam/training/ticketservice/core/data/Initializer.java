package com.epam.training.ticketservice.core.data;

import com.epam.training.ticketservice.core.model.User;
import com.epam.training.ticketservice.core.model.enums.Role;
import com.epam.training.ticketservice.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class Initializer {

    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", "admin", Role.ADMIN);
            userRepository.save(admin);
        }
    }

}
