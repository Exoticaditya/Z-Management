package com.zplus.adminpanel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DatabaseDebugController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/database-info")
    public ResponseEntity<Map<String, Object>> getDatabaseInfo() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            result.put("databaseProductName", metaData.getDatabaseProductName());
            result.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            result.put("databaseURL", metaData.getURL());
            result.put("userName", metaData.getUserName());
            result.put("catalogName", connection.getCatalog());
            result.put("schema", connection.getSchema());
            
            // Get all tables
            List<String> tables = new ArrayList<>();
            ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
            result.put("tables", tables);
            
            // Check if users table exists (case insensitive)
            List<String> userTables = new ArrayList<>();
            ResultSet userRs = metaData.getTables(null, null, "%user%", new String[]{"TABLE"});
            while (userRs.next()) {
                userTables.add(userRs.getString("TABLE_NAME"));
            }
            result.put("userRelatedTables", userTables);
            
            result.put("status", "success");
            result.put("message", "Database connection successful");
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/table-structure")
    public ResponseEntity<Map<String, Object>> getTableStructure() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            // Check for different case variations of users table
            String[] tableNames = {"users", "Users", "USERS", "user", "User", "USER"};
            
            for (String tableName : tableNames) {
                try {
                    ResultSet rs = metaData.getColumns(null, null, tableName, null);
                    List<Map<String, String>> columns = new ArrayList<>();
                    
                    while (rs.next()) {
                        Map<String, String> column = new HashMap<>();
                        column.put("columnName", rs.getString("COLUMN_NAME"));
                        column.put("dataType", rs.getString("TYPE_NAME"));
                        column.put("columnSize", rs.getString("COLUMN_SIZE"));
                        column.put("nullable", rs.getString("IS_NULLABLE"));
                        columns.add(column);
                    }
                    
                    if (!columns.isEmpty()) {
                        result.put("tableName", tableName);
                        result.put("columns", columns);
                        result.put("status", "found");
                        break;
                    }
                } catch (Exception e) {
                    // Continue to next table name
                }
            }
            
            if (!result.containsKey("status")) {
                result.put("status", "not_found");
                result.put("message", "No users table found with any case variation");
            }
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/create-users-table")
    public ResponseEntity<Map<String, Object>> createUsersTable() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS users (
                    id BIGSERIAL PRIMARY KEY,
                    self_id VARCHAR(255) UNIQUE NOT NULL,
                    username VARCHAR(255),
                    first_name VARCHAR(255),
                    last_name VARCHAR(255),
                    email VARCHAR(255),
                    phone VARCHAR(255),
                    password_hash VARCHAR(255) NOT NULL,
                    user_type VARCHAR(50) NOT NULL,
                    department VARCHAR(255),
                    position VARCHAR(255),
                    supervisor VARCHAR(255),
                    project_id VARCHAR(255),
                    reference_id VARCHAR(255),
                    profile_photo_url TEXT,
                    is_active BOOLEAN DEFAULT true,
                    status VARCHAR(50) DEFAULT 'ACTIVE',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    last_login TIMESTAMP,
                    approved_at TIMESTAMP,
                    approved_by VARCHAR(255),
                    rejected_at TIMESTAMP,
                    rejected_by VARCHAR(255),
                    rejection_reason TEXT
                )
                """;
            
            connection.createStatement().execute(createTableSQL);
            
            result.put("status", "success");
            result.put("message", "Users table created successfully");
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }
        
        return ResponseEntity.ok(result);
    }
}
