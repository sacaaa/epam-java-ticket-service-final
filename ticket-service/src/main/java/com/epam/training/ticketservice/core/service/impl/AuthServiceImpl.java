package com.epam.training.ticketservice.core.service.impl;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.dto.UserDto;
import com.epam.training.ticketservice.core.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserServiceImpl userService;

    public Result<Void> checkAdminPrivileges() {
        UserDto loggedInUser = userService.getLoggedInUser();
        if (loggedInUser == null || !loggedInUser.getRole().equals("ADMIN")) {
            return Result.failure("Error: Admin privileges are required.");
        }
        return Result.success(null);
    }

}
