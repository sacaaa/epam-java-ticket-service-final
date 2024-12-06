package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.dto.UserDto;
import com.epam.training.ticketservice.core.model.enums.Role;

import java.util.Optional;

/**
 * Service interface for managing user authentication and account operations.
 */
public interface UserService {

    /**
     * Signs in a user with the given username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return A {@code Result} containing the authenticated {@code UserDto} or
     *         an error message if the authentication fails.
     */
    Result<UserDto> signIn(String username, String password);

    /**
     * Signs in a user with the given username, password, and privileged access option.
     *
     * @param username   The username of the user.
     * @param password   The password of the user.
     * @param privileged Indicates if privileged access is required.
     * @return A {@code Result} containing the authenticated {@code UserDto} or
     *         an error message if the authentication fails.
     */
    Result<UserDto> signIn(String username, String password, boolean privileged);

    /**
     * Registers a new user with the given username, password, and role.
     *
     * @param username The username of the new user.
     * @param password The password of the new user.
     * @param role     The role assigned to the new user.
     * @return A {@code Result} containing the created {@code UserDto} or an error message if registration fails.
     */
    Result<UserDto> signUp(String username, String password, Role role);

    /**
     * Signs out the currently authenticated user.
     */
    void signOut();

    /**
     * Provides a description of the current user's session or privileges.
     *
     * @return A string describing the current user's session or role.
     */
    String describe();

    /**
     * Checks if the currently authenticated user has admin privileges.
     *
     * @return A {@code Result} indicating success or failure based on the user's privileges.
     */
    Result<Void> checkAdminPrivileges();

    /**
     * Retrieves the details of an authenticated user by their username.
     *
     * @param username The username of the user.
     * @return A {@code Result} containing the {@code UserDto} of the authenticated user or
     *         an error message if the user is not authenticated.
     */
    Result<UserDto> getAuthenticatedUser(String username);

}

