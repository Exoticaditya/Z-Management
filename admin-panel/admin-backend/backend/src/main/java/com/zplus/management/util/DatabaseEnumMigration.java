package com.zplus.management.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Database Migration Utility to create PostgreSQL enum types
 * This will run on application startup if the enums don't exist
 */
@Component
public class DatabaseEnumMigration implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        createEnumTypesIfNotExists();
    }

    private void createEnumTypesIfNotExists() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create user_type enum
            try {
                stmt.execute("CREATE TYPE user_type AS ENUM ('ADMIN', 'EMPLOYEE', 'CLIENT')");
                System.out.println("✅ Created user_type enum");
            } catch (Exception e) {
                if (e.getMessage().contains("already exists")) {
                    System.out.println("ℹ️ user_type enum already exists");
                } else {
                    System.out.println("⚠️ Error creating user_type enum: " + e.getMessage());
                }
            }

            // Create registration_status enum
            try {
                stmt.execute("CREATE TYPE registration_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'ACTIVE', 'INACTIVE')");
                System.out.println("✅ Created registration_status enum");
            } catch (Exception e) {
                if (e.getMessage().contains("already exists")) {
                    System.out.println("ℹ️ registration_status enum already exists");
                } else {
                    System.out.println("⚠️ Error creating registration_status enum: " + e.getMessage());
                }
            }

            // Create contact_status enum
            try {
                stmt.execute("CREATE TYPE contact_status AS ENUM ('NEW', 'IN_PROGRESS', 'RESOLVED', 'CLOSED')");
                System.out.println("✅ Created contact_status enum");
            } catch (Exception e) {
                if (e.getMessage().contains("already exists")) {
                    System.out.println("ℹ️ contact_status enum already exists");
                } else {
                    System.out.println("⚠️ Error creating contact_status enum: " + e.getMessage());
                }
            }

            // Create task_status enum
            try {
                stmt.execute("CREATE TYPE task_status AS ENUM ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')");
                System.out.println("✅ Created task_status enum");
            } catch (Exception e) {
                if (e.getMessage().contains("already exists")) {
                    System.out.println("ℹ️ task_status enum already exists");
                } else {
                    System.out.println("⚠️ Error creating task_status enum: " + e.getMessage());
                }
            }

            // Create task_priority enum
            try {
                stmt.execute("CREATE TYPE task_priority AS ENUM ('LOW', 'MEDIUM', 'HIGH', 'URGENT')");
                System.out.println("✅ Created task_priority enum");
            } catch (Exception e) {
                if (e.getMessage().contains("already exists")) {
                    System.out.println("ℹ️ task_priority enum already exists");
                } else {
                    System.out.println("⚠️ Error creating task_priority enum: " + e.getMessage());
                }
            }

            System.out.println("🎯 Database enum migration completed!");

        } catch (Exception e) {
            System.err.println("❌ Failed to create database enum types: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
