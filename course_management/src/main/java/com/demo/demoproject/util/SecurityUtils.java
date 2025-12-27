package com.demo.demoproject.util;

import org.springframework.security.core.context.SecurityContextHolder;

import com.demo.demoproject.modal.User;
import com.demo.demoproject.repo.UserRepository;

public class SecurityUtils {
    public static String currentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth == null) ? null : auth.getName();
    }

    public static User getCurrentUser(UserRepository userRepo) {
        String username = currentUsername();
        if(username == null) return null;
        return userRepo.findByUsername(username);
    }

    public static boolean currentUserIsAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    }
}
