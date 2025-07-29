package com.zplus.adminpanel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Database Test Controller for Railway Deployment
 */
@RestController
@RequestMapping("/api/test")
public class DatabaseTestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> testDatabaseConnection() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Test database connection
            Connection connection = dataSource.getConnection();
            String dbUrl = connection.getMetaData().getURL();
            String dbUsername = connection.getMetaData().getUserName();
            
            response.put("status", "SUCCESS");
            response.put("message", "Database connection successful");
            response.put("databaseUrl", dbUrl);
            response.put("username", dbUsername);
            response.put("databaseProduct", connection.getMetaData().getDatabaseProductName());
            response.put("databaseVersion", connection.getMetaData().getDatabaseProductVersion());
            
            connection.close();
            
        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", "Database connection failed");
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}
