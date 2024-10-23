package com.zeotap.ruleengineapp.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final String USER_ROLE = "USER";
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String DEFAULT_PASSWORD_HASH = "$2a$10$D9uFbENyoynRlIl1NxkZ3uZ/v39N8r.uJFe6XxOS/XEjcTAZ.z87i";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("user".equals(username)) {
            return User.builder()
                    .username("user")
                    .password(DEFAULT_PASSWORD_HASH)
                    .roles(USER_ROLE)
                    .build();
        } else if ("admin".equals(username)) {
            return User.builder()
                    .username("admin")
                    .password(DEFAULT_PASSWORD_HASH)
                    .roles(USER_ROLE, ADMIN_ROLE)
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found with username : " + username);
        }
    }
}
