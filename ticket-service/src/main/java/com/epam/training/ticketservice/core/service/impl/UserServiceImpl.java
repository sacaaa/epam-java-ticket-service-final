package com.epam.training.ticketservice.core.service.impl;

import com.epam.training.ticketservice.core.model.User;
import com.epam.training.ticketservice.core.model.dto.UserDto;
import com.epam.training.ticketservice.core.model.enums.Role;
import com.epam.training.ticketservice.core.repository.UserRepository;
import com.epam.training.ticketservice.core.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    @Getter
    private UserDto loggedInUser = null;

    private Optional<UserDto> signInInternal(String username, String password, boolean privileged) {
        Optional<User> userEntity = userRepository.findByUsernameAndPassword(username, password);

        if (userEntity.isPresent()) {
            User user = userEntity.get();

            if (!privileged && user.getRole().toString().equals("ADMIN")) {
                return Optional.empty();
            }

            UserDto userDto = objectMapper.convertValue(user, UserDto.class);
            String role = privileged ? "ADMIN" : user.getRole().toString();
            userDto.setRole(role);

            loggedInUser = userDto;
            return Optional.of(userDto);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserDto> signIn(String username, String password) {
        return signInInternal(username, password, false);
    }

    @Override
    public Optional<UserDto> signIn(String username, String password, boolean privileged) {
        return signInInternal(username, password, privileged);
    }

    @Override
    public Optional<UserDto> signUp(String username, String password, Role role) {
        if (userRepository.findByUsernameAndPassword(username, password).isPresent()) {
            return Optional.empty();
        }
        User user = new User(username, password, role);
        userRepository.save(user);
        return Optional.of(new UserDto(username, password, role.toString()));
    }

    @Override
    public void signOut() {
        loggedInUser = null;
    }

    @Override
    public String describe() {
        var res = "";

        if (loggedInUser == null) {
            res = "You are not signed in";
        } else {
            res = "Signed in with account '" + loggedInUser.getUsername() + "'";

            if (loggedInUser.getRole().equals("ADMIN")) {
                res = "Signed in with privileged account 'admin'";
            }
        }

        return res;
    }

}
