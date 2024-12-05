package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.model.dto.UserDto;
import com.epam.training.ticketservice.core.model.enums.Role;
import com.epam.training.ticketservice.core.service.impl.UserServiceImpl;
import com.epam.training.ticketservice.core.data.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserCommandTest {

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserCommand userCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignIn_Success() {
        // Arrange
        String username = "testUser";
        String password = "password";
        UserDto userDto = new UserDto(username, password, "USER");

        when(userService.signIn(username, password)).thenReturn(Result.success(userDto));

        // Act
        String result = userCommand.signIn(username, password);

        // Assert
        assertEquals("testUser successfully signed in", result);
        verify(userService).signIn(username, password);
    }

    @Test
    void testSignIn_Failure() {
        // Arrange
        String username = "testUser";
        String password = "wrongPassword";

        when(userService.signIn(username, password)).thenReturn(Result.failure("Login failed due to incorrect credentials"));

        // Act
        String result = userCommand.signIn(username, password);

        // Assert
        assertEquals("Login failed due to incorrect credentials", result);
        verify(userService).signIn(username, password);
    }

    @Test
    void testSignInPrivileged_Success() {
        // Arrange
        String username = "admin";
        String password = "password";
        UserDto userDto = new UserDto(username, password, "ADMIN");

        when(userService.signIn(username, password, true)).thenReturn(Result.success(userDto));

        // Act
        String result = userCommand.signInPrivileged(username, password);

        // Assert
        assertEquals("admin successfully signed in", result);
        verify(userService).signIn(username, password, true);
    }

    @Test
    void testSignInPrivileged_Failure() {
        // Arrange
        String username = "admin";
        String password = "wrongPassword";

        when(userService.signIn(username, password, true)).thenReturn(Result.failure("Login failed due to incorrect credentials"));

        // Act
        String result = userCommand.signInPrivileged(username, password);

        // Assert
        assertEquals("Login failed due to incorrect credentials", result);
        verify(userService).signIn(username, password, true);
    }

    @Test
    void testSignOut() {
        // Act
        String result = userCommand.signOut();

        // Assert
        assertEquals("Successfully signed out", result);
        verify(userService).signOut();
    }

    @Test
    void testDescribe() {
        // Arrange
        String description = "You are not signed in";
        when(userService.describe()).thenReturn(description);

        // Act
        String result = userCommand.describe();

        // Assert
        assertEquals(description, result);
        verify(userService).describe();
    }

    @Test
    void testSignUp_Success() {
        // Arrange
        String username = "newUser";
        String password = "password";

        when(userService.signUp(username, password, Role.USER)).thenReturn(Result.success(null));

        // Act
        String result = userCommand.signUp(username, password);

        // Assert
        assertEquals("User account 'newUser' created successfully.", result);
        verify(userService).signUp(username, password, Role.USER);
    }

    @Test
    void testSignUp_Failure() {
        // Arrange
        String username = "existingUser";
        String password = "password";

        when(userService.signUp(username, password, Role.USER))
                .thenReturn(Result.failure("Error: Username is already taken."));

        // Act
        String result = userCommand.signUp(username, password);

        // Assert
        assertEquals("Error: Username is already taken.", result);
        verify(userService).signUp(username, password, Role.USER);
    }
}
