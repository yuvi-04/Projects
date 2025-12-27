package com.demo.demoproject.security.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.demo.demoproject.repo.AuthUserRepository;
import com.demo.demoproject.security.model.AuthUser;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    @Autowired
    private AuthUserRepository authRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUser au = authRepo.findByUsername(username).orElseThrow(
            () -> new UsernameNotFoundException("User not found with username: " + username)
        );
        var authorities = au.getRoles().stream()
            .map(r -> new SimpleGrantedAuthority(r.getName()))
            .collect(Collectors.toList());
        return new User(
            au.getUsername(),
            au.getPassword(),
            au.isEnabled(),
            true, true, true, authorities
        );
    }

}
