package com.epam.training.ticketservice.core.service.impl;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.User;
import com.epam.training.ticketservice.core.model.dto.BookingDto;
import com.epam.training.ticketservice.core.model.dto.UserDto;
import com.epam.training.ticketservice.core.model.enums.Role;
import com.epam.training.ticketservice.core.repository.UserRepository;
import com.epam.training.ticketservice.core.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    private BookingServiceImpl bookingService;

    /**
     * The currently logged-in user. Null if no user is logged in.
     */
    @Getter
    private UserDto loggedInUser = null;

    /**
     * Sets the booking service lazily to avoid circular dependencies.
     *
     * @param bookingService The {@code BookingServiceImpl} to be set.
     */
    @Autowired
    public void setBookingService(@Lazy BookingServiceImpl bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Handles the internal sign-in logic, supporting privileged and non-privileged modes.
     *
     * @param username   The username of the user trying to sign in.
     * @param password   The user's password.
     * @param privileged Whether the user requires privileged (ADMIN) access.
     * @return A {@code Result} containing the authenticated {@code UserDto} or an error message if sign-in fails.
     */
    private Result<UserDto> signInInternal(String username, String password, boolean privileged) {
        Optional<User> userEntity = userRepository.findByUsernameAndPassword(username, password);

        if (userEntity.isPresent()) {
            User user = userEntity.get();

            if (!privileged && user.getRole().toString().equals("ADMIN")) {
                return Result.failure("Error: Admin privileges are required.");
            }

            UserDto userDto = objectMapper.convertValue(user, UserDto.class);
            String role = privileged ? "ADMIN" : user.getRole().toString();
            userDto.setRole(role);
            loggedInUser = userDto;

            return Result.success(userDto);
        } else {
            return Result.failure("Login failed due to incorrect credentials");
        }
    }

    @Override
    public Result<UserDto> signIn(String username, String password) {
        return signInInternal(username, password, false);
    }

    @Override
    public Result<UserDto> signIn(String username, String password, boolean privileged) {
        return signInInternal(username, password, privileged);
    }

    @Override
    public Result<UserDto> signUp(String username, String password, Role role) {
        if (userRepository.existsByUsername(username)) {
            return Result.failure("Error: Username '" + username + "' is already taken.");
        }

        User user = new User(username, password, role);
        userRepository.save(user);
        return Result.success(objectMapper.convertValue(user, UserDto.class));
    }

    @Override
    public void signOut() {
        loggedInUser = null;
    }

    @Override
    public String describe() {
        if (loggedInUser == null) {
            return "You are not signed in";
        }

        return loggedInUser.getRole().equals("ADMIN")
                ? "Signed in with privileged account 'admin'"
                : describeUserAccount(loggedInUser.getUsername());
    }

    /**
     * Describes the account of the currently logged-in user.
     *
     * @param username The username of the logged-in user.
     * @return A string description of the user's account and their booking history.
     */
    private String describeUserAccount(String username) {
        StringBuilder res = new StringBuilder("Signed in with account '").append(username).append("'");

        var bookingResult = bookingService.getBookingsByUser(username);
        if (bookingResult.isSuccess() && !bookingResult.getData().isEmpty()) {
            res.append("\nYour previous bookings are");
            bookingResult.getData().forEach(booking -> res.append(formatBooking(booking)));
        } else {
            res.append("\nYou have not booked any tickets yet");
        }

        return res.toString();
    }

    /**
     * Formats a booking into a readable string.
     *
     * @param booking The booking to format.
     * @return A formatted string representing the booking details.
     */
    private String formatBooking(BookingDto booking) {
        return String.format(
                "\nSeats %s on %s in room %s starting at %s for %d HUF",
                booking.getSeats().stream()
                        .map(seat -> "(" + seat + ")")
                        .collect(Collectors.joining(", ")),
                booking.getScreening().getMovie().getTitle(),
                booking.getScreening().getRoom().getName(),
                booking.getScreening().getStartTime()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                booking.getPrice()
        );
    }

    @Override
    public Result<Void> checkAdminPrivileges() {
        return (loggedInUser != null && "ADMIN".equals(loggedInUser.getRole()))
                ? Result.success(null)
                : Result.failure("Error: Admin privileges are required.");
    }

    @Override
    public Result<UserDto> getAuthenticatedUser(String username) {
        if (username == null || !username.equals(loggedInUser.getUsername())) {
            return Result.failure("Error: User is not authenticated.");
        }
        return Result.success(loggedInUser);
    }

}
