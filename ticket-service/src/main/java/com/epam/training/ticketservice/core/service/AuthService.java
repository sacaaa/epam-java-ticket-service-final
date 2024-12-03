package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.data.Result;

public interface AuthService {

    Result<Void> checkAdminPrivileges();

}
