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
        log.info("Attempting to authenticate with selfId: {}", loginRequest.getSelfId());

        try {
            Optional<User> userOptional = userRepository.findBySelfId(loginRequest.getSelfId());
            if (userOptional.isEmpty()) {
                log.warn("No user found with selfId: {}", loginRequest.getSelfId());
                throw new AuthenticationException("Invalid credentials");
            }
            
            User user = userOptional.get();
            log.debug("Found user: {} with userType: {}", user.getSelfId(), user.getUserType());
            
            if (user.getPasswordHash() == null) {
                log.error("User {} has null password hash", user.getSelfId());
                throw new AuthenticationException("Account configuration error");
            }
            
            log.debug("Checking password for user: {}", user.getSelfId());
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                log.warn("Password mismatch for user: {}", user.getSelfId());
                throw new AuthenticationException("Invalid credentials");
            }

            log.info("Authentication successful for user: {}", user.getSelfId());
            String token = jwtUtil.generateToken(user);
            return new LoginResponse(true, "Login successful", token, user.getUserType().toString(), user.getSelfId(), user.getFirstName() + " " + user.getLastName());
        } catch (AuthenticationException e) {
            log.warn("Authentication failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during authentication for user {}: {}", loginRequest.getSelfId(), e.getMessage(), e);
            throw new AuthenticationException("Authentication system error");
        }
    }
}
