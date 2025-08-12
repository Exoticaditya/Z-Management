package com.zplus.adminpanel.controller;

import com.zplus.adminpanel.entity.User;
import com.zplus.adminpanel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "*")
public class DatabaseConnectionDebugController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/users/count")
    public ResponseEntity<Map<String, Object>> getUserCount() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", userRepository.count());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/users/{selfId}")
    public ResponseEntity<Map<String, Object>> getUserBySelfId(@PathVariable String selfId) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> user = userRepository.findBySelfId(selfId);
        if (user.isPresent()) {
            User u = user.get();
            response.put("found", true);
            response.put("selfId", u.getSelfId());
            response.put("email", u.getEmail());
            response.put("name", u.getFirstName() + " " + u.getLastName());
            response.put("userType", u.getUserType().toString());
            response.put("isActive", u.getIsActive());
            response.put("hasPassword", u.getPasswordHash() != null && !u.getPasswordHash().isEmpty());
        } else {
            response.put("found", false);
        }
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/test-password")
    public ResponseEntity<Map<String, Object>> testPassword(@RequestParam String selfId, @RequestParam String password) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> userOpt = userRepository.findBySelfId(selfId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean matches = passwordEncoder.matches(password, user.getPasswordHash());
            response.put("found", true);
            response.put("passwordMatches", matches);
            response.put("hasPassword", user.getPasswordHash() != null && !user.getPasswordHash().isEmpty());
        } else {
            response.put("found", false);
        }
        
        return ResponseEntity.ok(response);
    }
}
