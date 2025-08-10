package com.zplus.adminpanel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fix-migration")
public class FixedMigrationController {

    @Autowired
    private DataSource dataSource;

    @PostMapping("/direct-migrate-users")
    public ResponseEntity<Map<String, Object>> directMigrateUsers(@RequestBody Map<String, String> oldDbConfig) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String oldHost = oldDbConfig.get("host");
            String oldPort = oldDbConfig.get("port");
            String oldDatabase = oldDbConfig.get("database");
            String oldUsername = oldDbConfig.get("username");
            String oldPassword = oldDbConfig.get("password");
            
            String oldDbUrl = String.format("jdbc:postgresql://%s:%s/%s", oldHost, oldPort, oldDatabase);
            
            // Connect to both databases
            Connection oldConn = DriverManager.getConnection(oldDbUrl, oldUsername, oldPassword);
            Connection newConn = dataSource.getConnection();
            newConn.setAutoCommit(false);
            
            try {
                // First, check the structure of both tables
                Map<String, Object> oldStructure = getTableStructure(oldConn, "users");
                Map<String, Object> newStructure = getTableStructure(newConn, "users");
                
                result.put("oldTableStructure", oldStructure);
                result.put("newTableStructure", newStructure);
                
                // Get users from old database
                Statement oldStmt = oldConn.createStatement();
                ResultSet oldUsers = oldStmt.executeQuery("""
                    SELECT self_id, username, first_name, last_name, email, phone, password_hash, 
                           user_type, department, position, supervisor, project_id, reference_id,
                           profile_photo_url, is_active, status, created_at, updated_at, last_login,
                           approved_at, approved_by, rejected_at, rejected_by, rejection_reason
                    FROM users
                    """);
                
                // Clear existing users in new database to avoid conflicts
                Statement clearStmt = newConn.createStatement();
                clearStmt.executeUpdate("DELETE FROM users");
                
                // Insert with proper handling of timestamps
                String insertSQL = """
                    INSERT INTO users (
                        self_id, username, first_name, last_name, email, phone, password_hash,
                        user_type, department, position, supervisor, project_id, reference_id,
                        profile_photo_url, is_active, status, 
                        created_at, updated_at, last_login,
                        approved_at, approved_by, rejected_at, rejected_by, rejection_reason
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
                
                PreparedStatement newStmt = newConn.prepareStatement(insertSQL);
                int count = 0;
                List<Map<String, Object>> migratedUsers = new ArrayList<>();
                
                while (oldUsers.next()) {
                    String selfId = oldUsers.getString("self_id");
                    String email = oldUsers.getString("email");
                    String firstName = oldUsers.getString("first_name");
                    String lastName = oldUsers.getString("last_name");
                    String userType = oldUsers.getString("user_type");
                    
                    // Set values with proper null handling
                    newStmt.setString(1, selfId);
                    newStmt.setString(2, oldUsers.getString("username"));
                    newStmt.setString(3, firstName);
                    newStmt.setString(4, lastName);
                    newStmt.setString(5, email);
                    newStmt.setString(6, oldUsers.getString("phone"));
                    newStmt.setString(7, oldUsers.getString("password_hash"));
                    newStmt.setString(8, userType);
                    newStmt.setString(9, oldUsers.getString("department"));
                    newStmt.setString(10, oldUsers.getString("position"));
                    newStmt.setString(11, oldUsers.getString("supervisor"));
                    newStmt.setString(12, oldUsers.getString("project_id"));
                    newStmt.setString(13, oldUsers.getString("reference_id"));
                    newStmt.setString(14, oldUsers.getString("profile_photo_url"));
                    newStmt.setBoolean(15, oldUsers.getBoolean("is_active"));
                    newStmt.setString(16, oldUsers.getString("status"));
                    
                    // Handle timestamps with defaults if null
                    Timestamp createdAt = oldUsers.getTimestamp("created_at");
                    if (createdAt == null) {
                        createdAt = new Timestamp(System.currentTimeMillis());
                    }
                    newStmt.setTimestamp(17, createdAt);
                    
                    Timestamp updatedAt = oldUsers.getTimestamp("updated_at");
                    if (updatedAt == null) {
                        updatedAt = new Timestamp(System.currentTimeMillis());
                    }
                    newStmt.setTimestamp(18, updatedAt);
                    
                    newStmt.setTimestamp(19, oldUsers.getTimestamp("last_login"));
                    newStmt.setTimestamp(20, oldUsers.getTimestamp("approved_at"));
                    newStmt.setString(21, oldUsers.getString("approved_by"));
                    newStmt.setTimestamp(22, oldUsers.getTimestamp("rejected_at"));
                    newStmt.setString(23, oldUsers.getString("rejected_by"));
                    newStmt.setString(24, oldUsers.getString("rejection_reason"));
                    
                    newStmt.executeUpdate();
                    count++;
                    
                    // Add to migrated users list
                    Map<String, Object> user = new HashMap<>();
                    user.put("selfId", selfId);
                    user.put("email", email);
                    user.put("firstName", firstName);
                    user.put("lastName", lastName);
                    user.put("userType", userType);
                    migratedUsers.add(user);
                }
                
                newConn.commit();
                
                result.put("status", "success");
                result.put("message", "Users migrated successfully with proper timestamp handling");
                result.put("usersMigrated", count);
                result.put("migratedUsers", migratedUsers);
                
            } catch (Exception e) {
                newConn.rollback();
                throw e;
            } finally {
                oldConn.close();
                newConn.close();
            }
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/verify-migration")
    public ResponseEntity<Map<String, Object>> verifyMigration() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            
            // Count users
            ResultSet countRs = stmt.executeQuery("SELECT COUNT(*) as user_count FROM users");
            int userCount = 0;
            if (countRs.next()) {
                userCount = countRs.getInt("user_count");
            }
            
            // Get all users
            ResultSet usersRs = stmt.executeQuery("""
                SELECT self_id, email, first_name, last_name, user_type, is_active, created_at 
                FROM users 
                ORDER BY self_id
                """);
            
            List<Map<String, Object>> users = new ArrayList<>();
            while (usersRs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("selfId", usersRs.getString("self_id"));
                user.put("email", usersRs.getString("email"));
                user.put("firstName", usersRs.getString("first_name"));
                user.put("lastName", usersRs.getString("last_name"));
                user.put("userType", usersRs.getString("user_type"));
                user.put("isActive", usersRs.getBoolean("is_active"));
                user.put("createdAt", usersRs.getTimestamp("created_at"));
                users.add(user);
            }
            
            result.put("status", "success");
            result.put("userCount", userCount);
            result.put("users", users);
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }
        
        return ResponseEntity.ok(result);
    }

    private Map<String, Object> getTableStructure(Connection conn, String tableName) throws SQLException {
        Map<String, Object> structure = new HashMap<>();
        List<Map<String, Object>> columns = new ArrayList<>();
        
        String sql = """
            SELECT column_name, data_type, is_nullable, column_default
            FROM information_schema.columns 
            WHERE table_name = ? 
            ORDER BY ordinal_position
            """;
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, tableName);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Map<String, Object> column = new HashMap<>();
            column.put("columnName", rs.getString("column_name"));
            column.put("dataType", rs.getString("data_type"));
            column.put("isNullable", rs.getString("is_nullable"));
            column.put("columnDefault", rs.getString("column_default"));
            columns.add(column);
        }
        
        structure.put("columns", columns);
        return structure;
    }
}
