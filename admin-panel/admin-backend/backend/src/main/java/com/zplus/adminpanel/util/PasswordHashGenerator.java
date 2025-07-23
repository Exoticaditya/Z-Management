package com.zplus.adminpanel.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PasswordHashGenerator implements CommandLineRunner {
    
    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && "generate-hash".equals(args[0])) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String password = "zplusadmin@123";
            String hashedPassword = passwordEncoder.encode(password);
            
            System.out.println("=== PASSWORD HASH GENERATOR ===");
            System.out.println("Password: " + password);
            System.out.println("BCrypt Hash: " + hashedPassword);
            
            // Verify the hash works
            boolean matches = passwordEncoder.matches(password, hashedPassword);
            System.out.println("Hash verification: " + matches);
            System.out.println("===============================");
            
            // Print SQL update command
            System.out.println("\nSQL UPDATE COMMAND:");
            System.out.println("UPDATE users SET password_hash = '" + hashedPassword + "' WHERE self_id = 'ZP001';");
            System.out.println("===============================");
        }
    }
}
