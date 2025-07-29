package com.zplus.adminpanel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.net.URI;

/**
 * Database configuration for Railway deployment
 */
@Configuration
@Profile("railway")
public class RailwayDatabaseConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Bean
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
            try {
                // Parse Railway DATABASE_URL
                URI dbUri = new URI(databaseUrl);
                
                String host = dbUri.getHost();
                int port = dbUri.getPort();
                String database = dbUri.getPath().substring(1); // Remove leading '/'
                String userInfo = dbUri.getUserInfo();
                String[] userParts = userInfo.split(":");
                String username = userParts[0];
                String password = userParts[1];
                
                // Construct JDBC URL
                String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
                
                System.out.println("🔧 Configuring Railway PostgreSQL DataSource");
                System.out.println("Host: " + host + ":" + port);
                System.out.println("Database: " + database);
                System.out.println("Username: " + username);
                
                // Create HikariCP configuration
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(jdbcUrl);
                config.setUsername(username);
                config.setPassword(password);
                config.setDriverClassName("org.postgresql.Driver");
                
                // Railway-optimized settings
                config.setMaximumPoolSize(3);
                config.setMinimumIdle(1);
                config.setConnectionTimeout(20000);
                config.setValidationTimeout(5000);
                config.setIdleTimeout(300000);
                config.setMaxLifetime(600000);
                config.setLeakDetectionThreshold(60000);
                
                // Connection properties
                config.addDataSourceProperty("socketTimeout", "30");
                config.addDataSourceProperty("loginTimeout", "30");
                config.addDataSourceProperty("connectTimeout", "30");
                config.addDataSourceProperty("ssl", "true");
                config.addDataSourceProperty("sslmode", "require");
                
                System.out.println("✅ Railway DataSource configured successfully");
                return new HikariDataSource(config);
                
            } catch (Exception e) {
                System.err.println("❌ Failed to parse DATABASE_URL: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Failed to configure Railway database", e);
            }
        } else {
            throw new RuntimeException("DATABASE_URL not found or invalid format");
        }
    }
}
