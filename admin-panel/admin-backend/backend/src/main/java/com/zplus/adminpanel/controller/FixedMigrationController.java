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

    @PostMapping("/check-all-tables")
    public ResponseEntity<Map<String, Object>> checkAllTables(@RequestBody Map<String, String> oldDbConfig) {
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
            
            try {
                // Get all tables from source database
                List<String> sourceTables = getAllTables(oldConn);
                List<String> targetTables = getAllTables(newConn);
                
                Map<String, Object> tableData = new HashMap<>();
                
                for (String tableName : sourceTables) {
                    Map<String, Object> tableInfo = new HashMap<>();
                    
                    // Get row count
                    Statement stmt = oldConn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
                    rs.next();
                    int rowCount = rs.getInt(1);
                    
                    // Get table structure
                    Map<String, Object> structure = getTableStructure(oldConn, tableName);
                    
                    tableInfo.put("rowCount", rowCount);
                    tableInfo.put("structure", structure);
                    tableInfo.put("existsInTarget", targetTables.contains(tableName));
                    
                    tableData.put(tableName, tableInfo);
                }
                
                result.put("status", "success");
                result.put("sourceTables", sourceTables);
                result.put("targetTables", targetTables);
                result.put("tableData", tableData);
                
            } finally {
                oldConn.close();
                newConn.close();
            }
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/migrate-specific-tables")
    public ResponseEntity<Map<String, Object>> migrateSpecificTables(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, String> oldDbConfig = (Map<String, String>) request.get("dbConfig");
            @SuppressWarnings("unchecked")
            List<String> tablesToMigrate = (List<String>) request.get("tables");
            Boolean clearTarget = (Boolean) request.getOrDefault("clearTarget", false);
            
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
                Map<String, Object> migrationResults = new HashMap<>();
                int totalMigrated = 0;
                
                for (String tableName : tablesToMigrate) {
                    // Clear target table if requested
                    if (clearTarget) {
                        Statement clearStmt = newConn.createStatement();
                        clearStmt.executeUpdate("TRUNCATE TABLE " + tableName + " RESTART IDENTITY CASCADE");
                        migrationResults.put(tableName + "_table_cleared", true);
                    }
                    
                    // Migrate data
                    int rowsMigrated = migrateTableData(oldConn, newConn, tableName);
                    migrationResults.put(tableName + "_rows_migrated", rowsMigrated);
                    totalMigrated += rowsMigrated;
                }
                
                newConn.commit();
                
                result.put("status", "success");
                result.put("totalRowsMigrated", totalMigrated);
                result.put("tablesProcessed", tablesToMigrate.size());
                result.put("migrationResults", migrationResults);
                
            } catch (Exception e) {
                newConn.rollback();
                throw e;
            } finally {
                oldConn.close();
                newConn.close();
            }
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/migrate-all-tables")
    public ResponseEntity<Map<String, Object>> migrateAllTables(@RequestBody Map<String, String> oldDbConfig) {
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
                // Get all tables from source database
                List<String> sourceTables = getAllTables(oldConn);
                List<String> targetTables = getAllTables(newConn);
                
                Map<String, Object> migrationResults = new HashMap<>();
                int totalMigrated = 0;
                
                for (String tableName : sourceTables) {
                    // Clear target table first to avoid duplicates
                    Statement clearStmt = newConn.createStatement();
                    clearStmt.executeUpdate("DELETE FROM " + tableName);
                    System.out.println("✅ Cleared existing data from table: " + tableName);
                    
                    if (!targetTables.contains(tableName)) {
                        // Create table if it doesn't exist
                        createTableFromSource(oldConn, newConn, tableName);
                        migrationResults.put(tableName + "_table_created", true);
                    }
                    
                    // Migrate data
                    int rowsMigrated = migrateTableData(oldConn, newConn, tableName);
                    migrationResults.put(tableName + "_rows_migrated", rowsMigrated);
                    totalMigrated += rowsMigrated;
                }
                
                newConn.commit();
                
                result.put("status", "success");
                result.put("totalRowsMigrated", totalMigrated);
                result.put("tablesProcessed", sourceTables.size());
                result.put("migrationResults", migrationResults);
                
            } catch (Exception e) {
                newConn.rollback();
                throw e;
            } finally {
                oldConn.close();
                newConn.close();
            }
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/migrate-non-user-tables")
    public ResponseEntity<Map<String, Object>> migrateNonUserTables(@RequestBody Map<String, String> oldDbConfig) {
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
                // Get all tables from source database, excluding users
                List<String> sourceTables = getAllTables(oldConn);
                sourceTables.remove("users"); // Skip users table as it's already migrated
                
                Map<String, Object> migrationResults = new HashMap<>();
                int totalMigrated = 0;
                
                for (String tableName : sourceTables) {
                    // Clear target table first to avoid duplicates
                    Statement clearStmt = newConn.createStatement();
                    clearStmt.executeUpdate("DELETE FROM " + tableName);
                    System.out.println("✅ Cleared existing data from table: " + tableName);
                    
                    // Migrate data
                    int rowsMigrated = migrateTableData(oldConn, newConn, tableName);
                    migrationResults.put(tableName + "_rows_migrated", rowsMigrated);
                    totalMigrated += rowsMigrated;
                }
                
                newConn.commit();
                
                result.put("status", "success");
                result.put("totalRowsMigrated", totalMigrated);
                result.put("tablesProcessed", sourceTables.size());
                result.put("migrationResults", migrationResults);
                result.put("skippedTables", "users (already migrated)");
                
            } catch (Exception e) {
                newConn.rollback();
                throw e;
            } finally {
                oldConn.close();
                newConn.close();
            }
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(result);
    }

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
    
    private List<String> getAllTables(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<>();
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet rs = metaData.getTables(null, "public", "%", new String[]{"TABLE"});
        
        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            // Skip system tables
            if (!tableName.startsWith("pg_") && !tableName.startsWith("information_schema")) {
                tables.add(tableName);
            }
        }
        
        return tables;
    }
    
    private void createTableFromSource(Connection sourceConn, Connection targetConn, String tableName) throws SQLException {
        // Get CREATE TABLE statement from source
        PreparedStatement stmt = sourceConn.prepareStatement("""
            SELECT 
                'CREATE TABLE ' || table_name || ' (' ||
                string_agg(
                    column_name || ' ' || 
                    CASE 
                        WHEN data_type = 'character varying' THEN 'VARCHAR(' || character_maximum_length || ')'
                        WHEN data_type = 'character' THEN 'CHAR(' || character_maximum_length || ')'
                        WHEN data_type = 'numeric' THEN 'NUMERIC(' || numeric_precision || ',' || numeric_scale || ')'
                        WHEN data_type = 'integer' THEN 'INTEGER'
                        WHEN data_type = 'bigint' THEN 'BIGINT'
                        WHEN data_type = 'boolean' THEN 'BOOLEAN'
                        WHEN data_type = 'timestamp without time zone' THEN 'TIMESTAMP'
                        WHEN data_type = 'timestamp with time zone' THEN 'TIMESTAMPTZ'
                        WHEN data_type = 'date' THEN 'DATE'
                        WHEN data_type = 'text' THEN 'TEXT'
                        ELSE UPPER(data_type)
                    END ||
                    CASE WHEN is_nullable = 'NO' THEN ' NOT NULL' ELSE '' END ||
                    CASE WHEN column_default IS NOT NULL THEN ' DEFAULT ' || column_default ELSE '' END,
                    ', '
                ) || ');' as create_statement
            FROM information_schema.columns
            WHERE table_name = ?
            GROUP BY table_name
            """);
        stmt.setString(1, tableName);
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            String createStatement = rs.getString("create_statement");
            Statement targetStmt = targetConn.createStatement();
            targetStmt.executeUpdate(createStatement);
            System.out.println("✅ Created table: " + tableName);
        }
    }
    
    private int migrateTableData(Connection sourceConn, Connection targetConn, String tableName) throws SQLException {
        // Get all data from source table
        Statement sourceStmt = sourceConn.createStatement();
        ResultSet rs = sourceStmt.executeQuery("SELECT * FROM " + tableName);
        
        // Get column metadata
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        // Build INSERT statement with special handling for enum types
        StringBuilder insertSQL = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder valuesSQL = new StringBuilder(" VALUES (");
        
        for (int i = 1; i <= columnCount; i++) {
            if (i > 1) {
                insertSQL.append(", ");
                valuesSQL.append(", ");
            }
            insertSQL.append(metaData.getColumnName(i));
            
            // Special handling for enum columns
            String columnTypeName = metaData.getColumnTypeName(i);
            if ("user_type".equals(columnTypeName) || "status".equals(columnTypeName)) {
                valuesSQL.append("CAST(? AS ").append(columnTypeName).append(")");
            } else {
                valuesSQL.append("?");
            }
        }
        insertSQL.append(")").append(valuesSQL).append(")");
        
        PreparedStatement insertStmt = targetConn.prepareStatement(insertSQL.toString());
        
        int rowCount = 0;
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                Object value = rs.getObject(i);
                if (value == null) {
                    insertStmt.setNull(i, metaData.getColumnType(i));
                } else {
                    insertStmt.setObject(i, value);
                }
            }
            insertStmt.executeUpdate();
            rowCount++;
        }
        
        System.out.println("✅ Migrated " + rowCount + " rows from table: " + tableName);
        return rowCount;
    }
    
    @PostMapping("/create-enum-types")
    public ResponseEntity<Map<String, Object>> createEnumTypes(@RequestBody Map<String, String> oldDbConfig) {
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
            
            try {
                // Create enum types in target database
                Statement stmt = newConn.createStatement();
                
                // Create user_type enum
                stmt.executeUpdate("CREATE TYPE user_type AS ENUM ('admin', 'employee', 'client')");
                System.out.println("✅ Created user_type enum");
                
                // Create status enum  
                stmt.executeUpdate("CREATE TYPE status AS ENUM ('active', 'inactive', 'pending', 'approved', 'rejected')");
                System.out.println("✅ Created status enum");
                
                result.put("status", "success");
                result.put("message", "Enum types created successfully");
                
            } catch (SQLException e) {
                if (e.getMessage().contains("already exists")) {
                    result.put("status", "info");
                    result.put("message", "Enum types already exist");
                } else {
                    throw e;
                }
            } finally {
                oldConn.close();
                newConn.close();
            }
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(result);
    }
}
