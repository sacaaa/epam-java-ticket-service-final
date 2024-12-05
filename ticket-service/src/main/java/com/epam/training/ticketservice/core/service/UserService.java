package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.dto.UserDto;
import com.epam.training.ticketservice.core.model.enums.Role;

import java.util.Optional;

public interface UserService {

    Result<UserDto> signIn(String username, String password);

    Result<UserDto> signIn(String username, String password, boolean privileged);

    Result<UserDto> signUp(String username, String password, Role role);

    void signOut();

    String describe();

    Result<Void> checkAdminPrivileges();

    Result<UserDto> getAuthenticatedUser(String username);

}
