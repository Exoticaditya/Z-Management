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
@RequestMapping("/api/migration")
public class DatabaseMigrationController {

    @Autowired
    private DataSource dataSource;

    @PostMapping("/migrate-from-old-database")
    public ResponseEntity<Map<String, Object>> migrateFromOldDatabase(@RequestBody Map<String, String> oldDbConfig) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Extract old database connection details
            String oldHost = oldDbConfig.get("host");
            String oldPort = oldDbConfig.get("port");
            String oldDatabase = oldDbConfig.get("database");
            String oldUsername = oldDbConfig.get("username");
            String oldPassword = oldDbConfig.get("password");
            
            if (oldHost == null || oldPort == null || oldDatabase == null || oldUsername == null || oldPassword == null) {
                result.put("status", "error");
                result.put("message", "Missing required connection parameters: host, port, database, username, password");
                return ResponseEntity.badRequest().body(result);
            }
            
            String oldDbUrl = String.format("jdbc:postgresql://%s:%s/%s", oldHost, oldPort, oldDatabase);
            
            // Connect to old database
            Connection oldConn = DriverManager.getConnection(oldDbUrl, oldUsername, oldPassword);
            
            // Connect to current database
            Connection newConn = dataSource.getConnection();
            newConn.setAutoCommit(false); // Use transactions
            
            try {
                // Migrate users data
                int usersMigrated = migrateUsersTable(oldConn, newConn);
                
                // Migrate other tables if they exist
                int contactInquiriesMigrated = migrateContactInquiries(oldConn, newConn);
                int registrationsMigrated = migrateRegistrations(oldConn, newConn);
                int projectsMigrated = migrateProjects(oldConn, newConn);
                
                // Commit all changes
                newConn.commit();
                
                result.put("status", "success");
                result.put("message", "Data migration completed successfully");
                result.put("usersMigrated", usersMigrated);
                result.put("contactInquiriesMigrated", contactInquiriesMigrated);
                result.put("registrationsMigrated", registrationsMigrated);
                result.put("projectsMigrated", projectsMigrated);
                
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

    @GetMapping("/check-old-database")
    public ResponseEntity<Map<String, Object>> checkOldDatabase(@RequestParam String host,
                                                               @RequestParam String port,
                                                               @RequestParam String database,
                                                               @RequestParam String username,
                                                               @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String oldDbUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
            Connection oldConn = DriverManager.getConnection(oldDbUrl, username, password);
            
            // Check what tables exist in old database
            DatabaseMetaData metaData = oldConn.getMetaData();
            ResultSet tables = metaData.getTables(null, "public", "%", new String[]{"TABLE"});
            
            List<String> tableNames = new ArrayList<>();
            while (tables.next()) {
                tableNames.add(tables.getString("TABLE_NAME"));
            }
            
            // Check users table data
            int userCount = 0;
            List<Map<String, Object>> sampleUsers = new ArrayList<>();
            
            if (tableNames.contains("users")) {
                Statement stmt = oldConn.createStatement();
                ResultSet userCountRs = stmt.executeQuery("SELECT COUNT(*) FROM users");
                if (userCountRs.next()) {
                    userCount = userCountRs.getInt(1);
                }
                
                // Get sample users
                ResultSet usersRs = stmt.executeQuery("SELECT self_id, email, first_name, last_name, user_type FROM users LIMIT 5");
                while (usersRs.next()) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("selfId", usersRs.getString("self_id"));
                    user.put("email", usersRs.getString("email"));
                    user.put("firstName", usersRs.getString("first_name"));
                    user.put("lastName", usersRs.getString("last_name"));
                    user.put("userType", usersRs.getString("user_type"));
                    sampleUsers.add(user);
                }
            }
            
            oldConn.close();
            
            result.put("status", "success");
            result.put("connectionSuccessful", true);
            result.put("tables", tableNames);
            result.put("userCount", userCount);
            result.put("sampleUsers", sampleUsers);
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("connectionSuccessful", false);
            result.put("message", e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }
        
        return ResponseEntity.ok(result);
    }

    private int migrateUsersTable(Connection oldConn, Connection newConn) throws SQLException {
        // First check if users table exists in old database
        Statement oldStmt = oldConn.createStatement();
        ResultSet oldUsers = oldStmt.executeQuery("""
            SELECT self_id, username, first_name, last_name, email, phone, password_hash, 
                   user_type, department, position, supervisor, project_id, reference_id,
                   profile_photo_url, is_active, status, created_at, updated_at, last_login,
                   approved_at, approved_by, rejected_at, rejected_by, rejection_reason
            FROM users
            """);
        
        String insertSQL = """
            INSERT INTO users (
                self_id, username, first_name, last_name, email, phone, password_hash,
                user_type, department, position, supervisor, project_id, reference_id,
                profile_photo_url, is_active, status, created_at, updated_at, last_login,
                approved_at, approved_by, rejected_at, rejected_by, rejection_reason
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (self_id) DO UPDATE SET
                username = EXCLUDED.username,
                first_name = EXCLUDED.first_name,
                last_name = EXCLUDED.last_name,
                email = EXCLUDED.email,
                phone = EXCLUDED.phone,
                password_hash = EXCLUDED.password_hash,
                user_type = EXCLUDED.user_type,
                department = EXCLUDED.department,
                position = EXCLUDED.position,
                supervisor = EXCLUDED.supervisor,
                project_id = EXCLUDED.project_id,
                reference_id = EXCLUDED.reference_id,
                profile_photo_url = EXCLUDED.profile_photo_url,
                is_active = EXCLUDED.is_active,
                status = EXCLUDED.status,
                updated_at = EXCLUDED.updated_at,
                last_login = EXCLUDED.last_login,
                approved_at = EXCLUDED.approved_at,
                approved_by = EXCLUDED.approved_by,
                rejected_at = EXCLUDED.rejected_at,
                rejected_by = EXCLUDED.rejected_by,
                rejection_reason = EXCLUDED.rejection_reason
            """;
        
        PreparedStatement newStmt = newConn.prepareStatement(insertSQL);
        int count = 0;
        
        while (oldUsers.next()) {
            newStmt.setString(1, oldUsers.getString("self_id"));
            newStmt.setString(2, oldUsers.getString("username"));
            newStmt.setString(3, oldUsers.getString("first_name"));
            newStmt.setString(4, oldUsers.getString("last_name"));
            newStmt.setString(5, oldUsers.getString("email"));
            newStmt.setString(6, oldUsers.getString("phone"));
            newStmt.setString(7, oldUsers.getString("password_hash"));
            newStmt.setString(8, oldUsers.getString("user_type"));
            newStmt.setString(9, oldUsers.getString("department"));
            newStmt.setString(10, oldUsers.getString("position"));
            newStmt.setString(11, oldUsers.getString("supervisor"));
            newStmt.setString(12, oldUsers.getString("project_id"));
            newStmt.setString(13, oldUsers.getString("reference_id"));
            newStmt.setString(14, oldUsers.getString("profile_photo_url"));
            newStmt.setBoolean(15, oldUsers.getBoolean("is_active"));
            newStmt.setString(16, oldUsers.getString("status"));
            newStmt.setTimestamp(17, oldUsers.getTimestamp("created_at"));
            newStmt.setTimestamp(18, oldUsers.getTimestamp("updated_at"));
            newStmt.setTimestamp(19, oldUsers.getTimestamp("last_login"));
            newStmt.setTimestamp(20, oldUsers.getTimestamp("approved_at"));
            newStmt.setString(21, oldUsers.getString("approved_by"));
            newStmt.setTimestamp(22, oldUsers.getTimestamp("rejected_at"));
            newStmt.setString(23, oldUsers.getString("rejected_by"));
            newStmt.setString(24, oldUsers.getString("rejection_reason"));
            
            newStmt.addBatch();
            count++;
        }
        
        if (count > 0) {
            newStmt.executeBatch();
        }
        
        return count;
    }

    private int migrateContactInquiries(Connection oldConn, Connection newConn) throws SQLException {
        try {
            Statement oldStmt = oldConn.createStatement();
            ResultSet oldData = oldStmt.executeQuery("SELECT * FROM contact_inquiries");
            
            // Get column metadata to build dynamic insert
            ResultSetMetaData metaData = oldData.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            StringBuilder insertSQL = new StringBuilder("INSERT INTO contact_inquiries (");
            StringBuilder valuesSQL = new StringBuilder(" VALUES (");
            
            for (int i = 1; i <= columnCount; i++) {
                if (i > 1) {
                    insertSQL.append(", ");
                    valuesSQL.append(", ");
                }
                insertSQL.append(metaData.getColumnName(i));
                valuesSQL.append("?");
            }
            insertSQL.append(")").append(valuesSQL).append(") ON CONFLICT DO NOTHING");
            
            PreparedStatement newStmt = newConn.prepareStatement(insertSQL.toString());
            int count = 0;
            
            while (oldData.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    newStmt.setObject(i, oldData.getObject(i));
                }
                newStmt.addBatch();
                count++;
            }
            
            if (count > 0) {
                newStmt.executeBatch();
            }
            
            return count;
        } catch (SQLException e) {
            // Table might not exist in old database
            return 0;
        }
    }

    private int migrateRegistrations(Connection oldConn, Connection newConn) throws SQLException {
        try {
            Statement oldStmt = oldConn.createStatement();
            ResultSet oldData = oldStmt.executeQuery("SELECT * FROM registrations");
            
            // Get column metadata
            ResultSetMetaData metaData = oldData.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            StringBuilder insertSQL = new StringBuilder("INSERT INTO registrations (");
            StringBuilder valuesSQL = new StringBuilder(" VALUES (");
            
            for (int i = 1; i <= columnCount; i++) {
                if (i > 1) {
                    insertSQL.append(", ");
                    valuesSQL.append(", ");
                }
                insertSQL.append(metaData.getColumnName(i));
                valuesSQL.append("?");
            }
            insertSQL.append(")").append(valuesSQL).append(") ON CONFLICT DO NOTHING");
            
            PreparedStatement newStmt = newConn.prepareStatement(insertSQL.toString());
            int count = 0;
            
            while (oldData.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    newStmt.setObject(i, oldData.getObject(i));
                }
                newStmt.addBatch();
                count++;
            }
            
            if (count > 0) {
                newStmt.executeBatch();
            }
            
            return count;
        } catch (SQLException e) {
            return 0;
        }
    }

    private int migrateProjects(Connection oldConn, Connection newConn) throws SQLException {
        try {
            Statement oldStmt = oldConn.createStatement();
            ResultSet oldData = oldStmt.executeQuery("SELECT * FROM projects");
            
            // Get column metadata
            ResultSetMetaData metaData = oldData.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            StringBuilder insertSQL = new StringBuilder("INSERT INTO projects (");
            StringBuilder valuesSQL = new StringBuilder(" VALUES (");
            
            for (int i = 1; i <= columnCount; i++) {
                if (i > 1) {
                    insertSQL.append(", ");
                    valuesSQL.append(", ");
                }
                insertSQL.append(metaData.getColumnName(i));
                valuesSQL.append("?");
            }
            insertSQL.append(")").append(valuesSQL).append(") ON CONFLICT DO NOTHING");
            
            PreparedStatement newStmt = newConn.prepareStatement(insertSQL.toString());
            int count = 0;
            
            while (oldData.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    newStmt.setObject(i, oldData.getObject(i));
                }
                newStmt.addBatch();
                count++;
            }
            
            if (count > 0) {
                newStmt.executeBatch();
            }
            
            return count;
        } catch (SQLException e) {
            return 0;
        }
    }
}
