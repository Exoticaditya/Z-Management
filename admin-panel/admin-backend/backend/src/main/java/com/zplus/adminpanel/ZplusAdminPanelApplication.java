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
            // Log environment check
            System.out.println("🔍 Environment Check:");
            System.out.println("PORT: " + System.getenv("PORT"));
            System.out.println("SPRING_PROFILES_ACTIVE: " + System.getenv("SPRING_PROFILES_ACTIVE"));
            System.out.println("PGHOST: " + System.getenv("PGHOST"));
            System.out.println("PGPORT: " + System.getenv("PGPORT"));
            System.out.println("PGDATABASE: " + System.getenv("PGDATABASE"));
            System.out.println("PGUSER: " + System.getenv("PGUSER"));
            
            SpringApplication.run(ZplusAdminPanelApplication.class, args);
        } catch (Exception e) {
            logger.error("❌ Failed to start Z+ Admin Panel Backend", e);
            System.exit(1);
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
    }
    
    private String maskPassword(String url) {
        if (url == null) return "null";
        return url.replaceAll(":[^:@]+@", ":***@");
    }
}
