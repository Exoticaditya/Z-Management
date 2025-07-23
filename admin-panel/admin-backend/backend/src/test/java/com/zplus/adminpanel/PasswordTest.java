package com.zplus.adminpanel;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = "zplusadmin@123";
        String hashedPassword = passwordEncoder.encode(password);
        
        System.out.println("=== PASSWORD HASH TEST ===");
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hashedPassword);
        
        // Verify the hash works
        boolean matches = passwordEncoder.matches(password, hashedPassword);
        System.out.println("Hash verification: " + matches);
        System.out.println("==========================");
    }
}
