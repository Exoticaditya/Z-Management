package com.zplus.adminpanel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Main application class for Z+ Admin Panel
 * 
 * @author Z+ Technologies
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableJpaRepositories(basePackages = "com.zplus.adminpanel.repository")
public class ZplusAdminPanelApplication {

    private static final Logger logger = LoggerFactory.getLogger(ZplusAdminPanelApplication.class);
    
    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        try {
            // Fix Railway DATABASE_URL format
            fixDatabaseUrl();
            
            // Log environment variables before starting
            System.out.println("🔍 Environment Check:");
            System.out.println("PORT: " + System.getenv("PORT"));
            System.out.println("DATABASE_URL present: " + (System.getenv("DATABASE_URL") != null));
            if (System.getenv("DATABASE_URL") != null) {
                String dbUrl = System.getenv("DATABASE_URL");
                System.out.println("DATABASE_URL format: " + dbUrl.replaceAll(":[^:@]+@", ":***@"));
            }
            
            SpringApplication.run(ZplusAdminPanelApplication.class, args);
        } catch (Exception e) {
            logger.error("❌ Failed to start Z+ Admin Panel Backend", e);
            System.exit(1);
        }
    }
    
    /**
     * Fix Railway DATABASE_URL format for Spring Boot compatibility
     * Railway: postgresql://user:pass@host:port/db
     * Spring: jdbc:postgresql://user:pass@host:port/db
     */
    private static void fixDatabaseUrl() {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
            try {
                // Parse the DATABASE_URL
                String fixedUrl = "jdbc:" + databaseUrl;
                
                // Extract components for debugging
                String[] parts = databaseUrl.replace("postgresql://", "").split("[@:/]");
                if (parts.length >= 4) {
                    String user = parts[0];
                    String host = parts.length > 2 ? parts[2] : "unknown";
                    String port = parts.length > 3 ? parts[3] : "5432";
                    String database = parts.length > 4 ? parts[4] : "railway";
                    
                    System.setProperty("spring.datasource.url", fixedUrl);
                    System.setProperty("SPRING_DATASOURCE_URL", fixedUrl);
                    
                    System.out.println("🔧 Fixed DATABASE_URL format for Spring Boot");
                    System.out.println("Host: " + host + ":" + port);
                    System.out.println("Database: " + database);
                    System.out.println("User: " + user);
                    System.out.println("Fixed URL set as system property");
                } else {
                    System.err.println("❌ Could not parse DATABASE_URL format: " + databaseUrl.replaceAll(":[^:@]+@", ":***@"));
                }
            } catch (Exception e) {
                System.err.println("❌ Error processing DATABASE_URL: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️  No DATABASE_URL found or not in postgresql:// format");
            if (databaseUrl != null) {
                System.out.println("DATABASE_URL: " + databaseUrl.replaceAll(":[^:@]+@", ":***@"));
            }
        }
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        String port = environment.getProperty("server.port", "8080");
        String profile = String.join(",", environment.getActiveProfiles());
        String databaseUrl = environment.getProperty("spring.datasource.url", "Not configured");
        
        logger.info("🚀 Z+ Admin Panel Backend started successfully!");
        logger.info("📊 Server Port: {}", port);
        logger.info("🔧 Active Profile: {}", profile);
        logger.info("💾 Database URL: {}", maskPassword(databaseUrl));
        logger.info("🌐 Health Check: http://localhost:{}/", port);
        
        // Log environment variables for debugging
        String rawDatabaseUrl = System.getenv("DATABASE_URL");
        if (rawDatabaseUrl != null) {
            logger.info("✅ DATABASE_URL environment variable is set");
            logger.info("🔗 Raw DATABASE_URL format: {}", maskPassword(rawDatabaseUrl));
        } else {
            logger.error("❌ DATABASE_URL environment variable is NOT set!");
        }
    }
    
    private String maskPassword(String url) {
        if (url == null) return "null";
        return url.replaceAll(":[^:@]+@", ":***@");
    }
}
