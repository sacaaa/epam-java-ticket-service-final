package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.model.enums.Role;
import com.epam.training.ticketservice.core.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class UserCommand {

    private final UserServiceImpl userService;

    @ShellMethod(key = "sign in", value = "Sign in with your username and password")
    public String signIn(String username, String password) {
        var result = userService.signIn(username, password);
        return result.isSuccess()
                ? result.getData().getUsername() + " successfully signed in"
                : result.getMessage();
    }

    @ShellMethod(key = "sign in privileged", value = "Sign in with your privileged username and password")
    public String signInPrivileged(String username, String password) {
        var result = userService.signIn(username, password, true);
        return result.isSuccess()
                ? result.getData().getUsername() + " successfully signed in"
                : result.getMessage();
    }

    @ShellMethod(key = "sign out", value = "Sign out from the application")
    public String signOut() {
        userService.signOut();
        return "Successfully signed out";
    }

    @ShellMethod(key = "describe account", value = "Describes the current account")
    public String describe() {
        return userService.describe();
    }

    @ShellMethod(key = "sign up", value = "Create a new user account")
    public String signUp(String username, String password) {
        var result = userService.signUp(username, password, Role.USER);
        return result.isSuccess()
                ? String.format("User account '%s' created successfully.", username)
                : result.getMessage();
    }

}
