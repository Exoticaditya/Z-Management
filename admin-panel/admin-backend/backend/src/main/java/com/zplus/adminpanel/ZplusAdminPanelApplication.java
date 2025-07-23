package com.zplus.adminpanel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

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

    public static void main(String[] args) {
        SpringApplication.run(ZplusAdminPanelApplication.class, args);
        System.out.println("🚀 Z+ Admin Panel Backend is running!");
        System.out.println("📊 Dashboard: http://localhost:8080/");
        System.out.println("📖 API Documentation: http://localhost:8080/api/swagger-ui.html");
    }
}
