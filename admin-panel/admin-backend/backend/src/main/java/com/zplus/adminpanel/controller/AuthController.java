package com.zplus.adminpanel.controller;

import com.zplus.adminpanel.dto.*;
import com.zplus.adminpanel.service.AuthService;
import com.zplus.adminpanel.service.UserService;
import com.zplus.adminpanel.util.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
    "http://localhost:5500",
    "http://127.0.0.1:5500", 
    "http://localhost:8080",
    "https://zpluse.com",
    "https://www.zpluse.com",
    "http://zpluse.com",
    "http://www.zpluse.com"
})
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, UserService userService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for user: {}", loginRequest.getSelfId());
            LoginResponse response = authService.authenticate(loginRequest);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            logger.warn("Login failed for user: {}", loginRequest.getSelfId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, e.getMessage(), null, null, null, null));
        }
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        try {
       	 logger.info("Registration attempt for user: {}", registrationRequest.getSelfId());
			RegistrationResponse response = userService.registerUser(registrationRequest);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Registration failed for user: {}", registrationRequest.getSelfId(), e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new RegistrationResponse(false, e.getMessage(), null));
		}
    }

    @GetMapping("/verify-token")
    public ResponseEntity<ApiResponse<String>> verifyToken(@RequestHeader("Authorization") String token) {
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            return ResponseEntity.ok(ApiResponse.<String>success("Token is valid", username));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<String>error("Invalid token", null));
        }
    }
}