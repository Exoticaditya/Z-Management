package com.zplus.adminpanel.config;

import com.zplus.adminpanel.repository.UserRepository;
import com.zplus.adminpanel.service.UserService;
import com.zplus.adminpanel.service.impl.UserServiceImpl;
import com.zplus.adminpanel.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ServiceConfig {
    
    @Bean
    public UserService userService(UserRepository userRepository, 
                                 PasswordEncoder passwordEncoder,
                                 JwtUtil jwtUtil) {
        return new UserServiceImpl(userRepository, null, passwordEncoder, jwtUtil);
    }
}
