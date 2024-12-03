package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.model.dto.UserDto;
import com.epam.training.ticketservice.core.model.enums.Role;

import java.util.Optional;

public interface UserService {

    Optional<UserDto> signIn(String username, String password);

    Optional<UserDto> signIn(String username, String password, boolean privileged);

    Optional<UserDto> signUp(String username, String password, Role role);

    void signOut();

    String describe();

}
