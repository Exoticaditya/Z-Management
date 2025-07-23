package com.zplus.adminpanel.service;

import com.zplus.adminpanel.dto.LoginRequest;
import com.zplus.adminpanel.dto.LoginResponse;

public interface AuthService {
    LoginResponse authenticate(LoginRequest loginRequest);
}
