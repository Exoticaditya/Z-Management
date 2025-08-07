package com.zplus.adminpanel.config;

import com.zplus.adminpanel.entity.User;
import com.zplus.adminpanel.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomerUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String selfId) throws UsernameNotFoundException {
        User user = userRepository.findBySelfId(selfId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with selfId: " + selfId));
        
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getSelfId())
                .password(user.getPasswordHash()) // Using getPasswordHash() to match the entity field
                .authorities(new SimpleGrantedAuthority("ROLE_" + user.getUserType().name()))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(user.getIsActive() != null && !user.getIsActive())
                .build();
    }

    /**
     * Helper method to get the full User entity by selfId
     */
    public User getUserBySelfId(String selfId) {
        return userRepository.findBySelfId(selfId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with selfId: " + selfId));
    }
}