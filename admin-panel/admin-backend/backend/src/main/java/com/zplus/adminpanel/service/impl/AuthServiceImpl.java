package com.zplus.adminpanel.service.impl;

import com.zplus.adminpanel.dto.LoginRequest;
import com.zplus.adminpanel.dto.LoginResponse;
import com.zplus.adminpanel.entity.User;
import com.zplus.adminpanel.exception.AuthenticationException;
import com.zplus.adminpanel.repository.UserRepository;
import com.zplus.adminpanel.service.AuthService;
import com.zplus.adminpanel.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthServiceImpl(PasswordEncoder passwordEncoder, 
                          JwtUtil jwtUtil,
                          UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public LoginResponse authenticate(LoginRequest loginRequest) {
        log.debug("Attempting to authenticate with selfId: {}", loginRequest.getSelfId());

        User user = userRepository.findBySelfId(loginRequest.getSelfId())
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user);
        return new LoginResponse(true, "Login successful", token, user.getUserType().toString(), user.getSelfId(), user.getFirstName() + " " + user.getLastName());
    }
}
