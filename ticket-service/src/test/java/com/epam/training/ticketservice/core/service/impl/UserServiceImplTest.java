package com.epam.training.ticketservice.core.service.impl;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.User;
import com.epam.training.ticketservice.core.model.dto.*;
import com.epam.training.ticketservice.core.model.enums.Role;
import com.epam.training.ticketservice.core.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private BookingServiceImpl bookingService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService.setBookingService(bookingService);
    }

    @Test
    void testSignInSuccess() {
        User mockUser = new User("testUser", "password", Role.USER);
        UserDto mockUserDto = new UserDto("testUser", "password", "USER");

        when(userRepository.findByUsernameAndPassword("testUser", "password"))
                .thenReturn(Optional.of(mockUser));
        when(objectMapper.convertValue(mockUser, UserDto.class)).thenReturn(mockUserDto);

        Result<UserDto> result = userService.signIn("testUser", "password");

        assertTrue(result.isSuccess());
        assertEquals("testUser", result.getData().getUsername());
        verify(userRepository).findByUsernameAndPassword("testUser", "password");
    }

    @Test
    void testSignInFailure() {
        when(userRepository.findByUsernameAndPassword("testUser", "password"))
                .thenReturn(Optional.empty());

        Result<UserDto> result = userService.signIn("testUser", "password");

        assertFalse(result.isSuccess());
        verify(userRepository).findByUsernameAndPassword("testUser", "password");
    }

    @Test
    void testSignUpSuccess() {
        UserDto mockUserDto = new UserDto("newUser", "password", "USER");
        User mockUser = new User("newUser", "password", Role.USER);

        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(objectMapper.convertValue(mockUser, UserDto.class)).thenReturn(mockUserDto);

        Result<UserDto> result = userService.signUp("newUser", "password", Role.USER);

        assertTrue(result.isSuccess());
        verify(userRepository).existsByUsername("newUser");
        verify(userRepository).save(mockUser);
    }

    @Test
    void testSignUpFailure() {
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        Result<UserDto> result = userService.signUp("existingUser", "password", Role.USER);

        assertFalse(result.isSuccess());
        assertEquals("Error: Username 'existingUser' is already taken.", result.getMessage());
        verify(userRepository).existsByUsername("existingUser");
    }

    @Test
    void testSignOut() {
        // Arrange
        when(userRepository.findByUsernameAndPassword("testUser", "password"))
                .thenReturn(Optional.of(new User("testUser", "password", Role.USER)));
        when(objectMapper.convertValue(any(User.class), eq(UserDto.class)))
                .thenReturn(new UserDto("testUser", "password", "USER"));
        userService.signIn("testUser", "password");

        // Act
        userService.signOut();

        // Assert
        assertNull(userService.getLoggedInUser());
    }

    @Test
    void testDescribeWhenNotSignedIn() {
        // Act
        String description = userService.describe();

        // Assert
        assertEquals("You are not signed in", description);
    }

    @Test
    void testDescribeAsAdmin() {
        // Arrange
        when(userRepository.findByUsernameAndPassword("admin", "password"))
                .thenReturn(Optional.of(new User("admin", "password", Role.ADMIN)));
        when(objectMapper.convertValue(any(User.class), eq(UserDto.class)))
                .thenReturn(new UserDto("admin", "password", "ADMIN"));
        userService.signIn("admin", "password", true);

        // Act
        String description = userService.describe();

        // Assert
        assertEquals("Signed in with privileged account 'admin'", description);
    }

    @Test
    void testDescribeUserAccountWithoutBooking() {
        // Arrange
        when(userRepository.findByUsernameAndPassword("testUser", "password"))
                .thenReturn(Optional.of(new User("testUser", "password", Role.USER)));
        when(objectMapper.convertValue(any(User.class), eq(UserDto.class)))
                .thenReturn(new UserDto("testUser", "password", "USER"));
        userService.signIn("testUser", "password");
        when(bookingService.getBookingsByUser("testUser"))
                .thenReturn(Result.success(Collections.emptyList()));

        // Act
        String description = userService.describe();

        // Assert
        assertEquals("Signed in with account 'testUser'\nYou have not booked any tickets yet", description);
    }

    @Test
    void testDescribeUserAccountWithBooking() {
        // Arrange
        var mockuserDto = new UserDto("testUser", "password", "USER");
        when(userRepository.findByUsernameAndPassword("testUser", "password"))
                .thenReturn(Optional.of(new User("testUser", "password", Role.USER)));
        when(objectMapper.convertValue(any(User.class), eq(UserDto.class)))
                .thenReturn(new UserDto("testUser", "password", "USER"));
        userService.signIn("testUser", "password");

        // Create a mock booking
        MovieDto movieDto = new MovieDto("Movie Title", "drama", 100, new HashSet<>());
        RoomDto roomDto = new RoomDto("Room Name", 1, 1, 1, new HashSet<>());
        ScreeningDto screeningDto = new ScreeningDto(movieDto, roomDto, LocalDateTime.now(), new HashSet<>());
        BookingDto bookingDto = new BookingDto(mockuserDto, screeningDto, List.of("1,1"), 1500, LocalDateTime.now());
        when(bookingService.getBookingsByUser("testUser"))
                .thenReturn(Result.success(List.of(bookingDto)));

        // Act
        String description = userService.describe();

        // Assert
        assertTrue(description.contains("Signed in with account 'testUser'"));
        assertTrue(description.contains("Your previous bookings are"));
        assertTrue(description.contains("Seats"));
        assertTrue(description.contains("on Movie Title in room Room Name"));
    }

    @Test
    void testCheckAdminPrivilegesWithAdmin() {
        // Arrange
        when(userRepository.findByUsernameAndPassword("admin", "password"))
                .thenReturn(Optional.of(new User("admin", "password", Role.ADMIN)));
        when(objectMapper.convertValue(any(User.class), eq(UserDto.class)))
                .thenReturn(new UserDto("admin", "password", "ADMIN"));
        userService.signIn("admin", "password", true);

        // Act
        Result<Void> result = userService.checkAdminPrivileges();

        // Assert
        assertTrue(result.isSuccess());
    }

    @Test
    void testCheckAdminPrivilegesWithoutAdmin() {
        // Arrange
        when(userRepository.findByUsernameAndPassword("testUser", "password"))
                .thenReturn(Optional.of(new User("testUser", "password", Role.USER)));
        when(objectMapper.convertValue(any(User.class), eq(UserDto.class)))
                .thenReturn(new UserDto("testUser", "password", "USER"));
        userService.signIn("testUser", "password");

        // Act
        Result<Void> result = userService.checkAdminPrivileges();

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Admin privileges are required.", result.getMessage());
    }

    @Test
    void testGetAuthenticatedUserSuccess() {
        // Arrange
        when(userRepository.findByUsernameAndPassword("testUser", "password"))
                .thenReturn(Optional.of(new User("testUser", "password", Role.USER)));
        when(objectMapper.convertValue(any(User.class), eq(UserDto.class)))
                .thenReturn(new UserDto("testUser", "password", "USER"));
        userService.signIn("testUser", "password");

        // Act
        Result<UserDto> result = userService.getAuthenticatedUser("testUser");

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("testUser", result.getData().getUsername());
    }

    @Test
    void testGetAuthenticatedUserFailure() {
        // Arrange
        when(userRepository.findByUsernameAndPassword("testUser", "password"))
                .thenReturn(Optional.of(new User("testUser", "password", Role.USER)));
        when(objectMapper.convertValue(any(User.class), eq(UserDto.class)))
                .thenReturn(new UserDto("testUser", "password", "USER"));
        userService.signIn("testUser", "password");

        // Act
        Result<UserDto> result = userService.getAuthenticatedUser("wrongUser");

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: User is not authenticated.", result.getMessage());
    }

    @Test
    void testSignInAsAdminWithoutPrivileges_ShouldFail() {
        // Arrange
        User adminUser = new User("adminUser", "password", Role.ADMIN);

        when(userRepository.findByUsernameAndPassword("adminUser", "password"))
                .thenReturn(Optional.of(adminUser));

        // Act
        Result<UserDto> result = userService.signIn("adminUser", "password");

        // Assert
        assertFalse(result.isSuccess());
        verify(userRepository).findByUsernameAndPassword("adminUser", "password");
        verifyNoInteractions(objectMapper);
    }
}