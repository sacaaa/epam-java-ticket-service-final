package com.epam.training.ticketservice.core.data;

import static org.mockito.Mockito.*;

import com.epam.training.ticketservice.core.model.User;
import com.epam.training.ticketservice.core.model.enums.Role;
import com.epam.training.ticketservice.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class InitializerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private Initializer initializer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInit_WhenAdminUserDoesNotExist() {
        // Arrange
        when(userRepository.existsByUsername("admin")).thenReturn(false);

        // Act
        initializer.init();

        // Assert
        verify(userRepository, times(1)).save(argThat(user ->
                user.getUsername().equals("admin") &&
                        user.getPassword().equals("admin") &&
                        user.getRole() == Role.ADMIN
        ));
    }

    @Test
    void testInit_WhenAdminUserAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        // Act
        initializer.init();

        // Assert
        verify(userRepository, never()).save(any(User.class));
    }
}
