package com.zplus.adminpanel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DatabaseConnectionDebugController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/raw-sql-check")
    public ResponseEntity<Map<String, Object>> rawSqlCheck() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            
            // Check all tables in all schemas
            String sql = """
                SELECT schemaname, tablename 
                FROM pg_tables 
                WHERE schemaname NOT IN ('information_schema', 'pg_catalog')
                ORDER BY schemaname, tablename
                """;
            
            ResultSet rs = stmt.executeQuery(sql);
            List<Map<String, String>> allTables = new ArrayList<>();
            
            while (rs.next()) {
                Map<String, String> table = new HashMap<>();
                table.put("schema", rs.getString("schemaname"));
                table.put("table", rs.getString("tablename"));
                allTables.add(table);
            }
            
            result.put("allTables", allTables);
            
            // Specifically look for users table
            String userCheckSql = """
                SELECT schemaname, tablename 
                FROM pg_tables 
                WHERE tablename ILIKE '%user%'
                """;
            
            ResultSet userRs = stmt.executeQuery(userCheckSql);
            List<Map<String, String>> userTables = new ArrayList<>();
            
            while (userRs.next()) {
                Map<String, String> table = new HashMap<>();
                table.put("schema", userRs.getString("schemaname"));
                table.put("table", userRs.getString("tablename"));
                userTables.add(table);
            }
            
            result.put("userTables", userTables);
            
            // Try to query the users table directly
            try {
                String directSql = "SELECT COUNT(*) as user_count FROM users";
                ResultSet countRs = stmt.executeQuery(directSql);
                if (countRs.next()) {
                    result.put("usersTableExists", true);
                    result.put("userCount", countRs.getInt("user_count"));
                }
            } catch (Exception e) {
                result.put("usersTableExists", false);
                result.put("usersTableError", e.getMessage());
            }
            
            // Try with schema prefix
            try {
                String schemaSql = "SELECT COUNT(*) as user_count FROM public.users";
                ResultSet schemaRs = stmt.executeQuery(schemaSql);
                if (schemaRs.next()) {
                    result.put("publicUsersExists", true);
                    result.put("publicUserCount", schemaRs.getInt("user_count"));
                }
            } catch (Exception e) {
                result.put("publicUsersExists", false);
                result.put("publicUsersError", e.getMessage());
            }
            
            // Check current schema
            ResultSet currentSchemaRs = stmt.executeQuery("SELECT current_schema()");
            if (currentSchemaRs.next()) {
                result.put("currentSchema", currentSchemaRs.getString(1));
            }
            
            // Check search path
            ResultSet searchPathRs = stmt.executeQuery("SHOW search_path");
            if (searchPathRs.next()) {
                result.put("searchPath", searchPathRs.getString(1));
            }
            
            result.put("status", "success");
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/test-user-query")
    public ResponseEntity<Map<String, Object>> testUserQuery() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            
            // Try to select from users table
            String sql = "SELECT self_id, email, first_name, last_name, user_type FROM users LIMIT 5";
            ResultSet rs = stmt.executeQuery(sql);
            
            List<Map<String, Object>> users = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("selfId", rs.getString("self_id"));
                user.put("email", rs.getString("email"));
                user.put("firstName", rs.getString("first_name"));
                user.put("lastName", rs.getString("last_name"));
                user.put("userType", rs.getString("user_type"));
                users.add(user);
            }
            
            result.put("status", "success");
            result.put("users", users);
            result.put("userCount", users.size());
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/insert-test-user")
    public ResponseEntity<Map<String, Object>> insertTestUser() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            
            // First, let's check the table structure
            String checkStructureSQL = """
                SELECT column_name, data_type, is_nullable, column_default
                FROM information_schema.columns 
                WHERE table_name = 'users' 
                ORDER BY ordinal_position
                """;
            
            Statement stmt = connection.createStatement();
            ResultSet structureRs = stmt.executeQuery(checkStructureSQL);
            List<Map<String, Object>> structure = new ArrayList<>();
            
            while (structureRs.next()) {
                Map<String, Object> column = new HashMap<>();
                column.put("columnName", structureRs.getString("column_name"));
                column.put("dataType", structureRs.getString("data_type"));
                column.put("isNullable", structureRs.getString("is_nullable"));
                column.put("columnDefault", structureRs.getString("column_default"));
                structure.add(column);
            }
            result.put("tableStructure", structure);
            
            // Insert user with proper handling of all required fields
            String insertSQL = """
                INSERT INTO users (
                    self_id, username, first_name, last_name, email, 
                    password_hash, user_type, is_active, status,
                    created_at, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                ON CONFLICT (self_id) DO UPDATE SET
                    username = EXCLUDED.username,
                    updated_at = CURRENT_TIMESTAMP
                """;
            
            var preparedStmt = connection.prepareStatement(insertSQL);
            preparedStmt.setString(1, "admin");
            preparedStmt.setString(2, "admin");
            preparedStmt.setString(3, "Admin");
            preparedStmt.setString(4, "User");
            preparedStmt.setString(5, "admin@zplus.com");
            preparedStmt.setString(6, "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.");
            preparedStmt.setString(7, "ADMIN");
            preparedStmt.setBoolean(8, true);
            preparedStmt.setString(9, "ACTIVE");
            
            int rowsAffected = preparedStmt.executeUpdate();
            
            result.put("status", "success");
            result.put("message", "Admin user inserted/updated successfully");
            result.put("rowsAffected", rowsAffected);
            
            // Verify the user was inserted
            String verifySQL = "SELECT self_id, email, first_name, last_name, user_type FROM users WHERE self_id = 'admin'";
            ResultSet verifyRs = stmt.executeQuery(verifySQL);
            if (verifyRs.next()) {
                Map<String, Object> insertedUser = new HashMap<>();
                insertedUser.put("selfId", verifyRs.getString("self_id"));
                insertedUser.put("email", verifyRs.getString("email"));
                insertedUser.put("firstName", verifyRs.getString("first_name"));
                insertedUser.put("lastName", verifyRs.getString("last_name"));
                insertedUser.put("userType", verifyRs.getString("user_type"));
                result.put("insertedUser", insertedUser);
            }
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/insert-multiple-users")
    public ResponseEntity<Map<String, Object>> insertMultipleUsers() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            
            String insertSQL = """
                INSERT INTO users (
                    self_id, username, first_name, last_name, email, 
                    password_hash, user_type, is_active, status,
                    created_at, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                ON CONFLICT (self_id) DO UPDATE SET
                    username = EXCLUDED.username,
                    updated_at = CURRENT_TIMESTAMP
                """;
            
            var preparedStmt = connection.prepareStatement(insertSQL);
            
            // Admin user
            preparedStmt.setString(1, "admin");
            preparedStmt.setString(2, "admin");
            preparedStmt.setString(3, "Admin");
            preparedStmt.setString(4, "User");
            preparedStmt.setString(5, "admin@zplus.com");
            preparedStmt.setString(6, "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.");
            preparedStmt.setString(7, "ADMIN");
            preparedStmt.setBoolean(8, true);
            preparedStmt.setString(9, "ACTIVE");
            preparedStmt.addBatch();
            
            // Employee user  
            preparedStmt.setString(1, "john");
            preparedStmt.setString(2, "john.employee");
            preparedStmt.setString(3, "John");
            preparedStmt.setString(4, "Employee");
            preparedStmt.setString(5, "john.employee@zplus.com");
            preparedStmt.setString(6, "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.");
            preparedStmt.setString(7, "EMPLOYEE");
            preparedStmt.setBoolean(8, true);
            preparedStmt.setString(9, "ACTIVE");
            preparedStmt.addBatch();
            
            // Client user
            preparedStmt.setString(1, "mike");
            preparedStmt.setString(2, "mike.client");
            preparedStmt.setString(3, "Mike");
            preparedStmt.setString(4, "Client");
            preparedStmt.setString(5, "mike.client@example.com");
            preparedStmt.setString(6, "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.");
            preparedStmt.setString(7, "CLIENT");
            preparedStmt.setBoolean(8, true);
            preparedStmt.setString(9, "ACTIVE");
            preparedStmt.addBatch();
            
            int[] rowsAffected = preparedStmt.executeBatch();
            
            result.put("status", "success");
            result.put("message", "Multiple users inserted successfully");
            result.put("usersCreated", rowsAffected.length);
            result.put("rowsAffected", rowsAffected);
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(result);
    }
}
