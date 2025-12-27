package com.demo.demoproject.config;

import java.util.Optional;
import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.demo.demoproject.modal.User;
import com.demo.demoproject.modal.UserProfile;
import com.demo.demoproject.repo.AuthUserRepository;
import com.demo.demoproject.repo.RoleRepository;
import com.demo.demoproject.repo.UserRepository;
import com.demo.demoproject.security.model.AuthUser;
import com.demo.demoproject.security.model.Role;

@Configuration
public class SecuritySeeder {
    @Bean
    public ApplicationRunner seed(RoleRepository roleRepo, AuthUserRepository authRepo, UserRepository userRepo, PasswordEncoder encoder) {
        return args -> {
            roleRepo.findByName("ROLE_USER").or(() -> {
                Role r = new Role();
                r.setName("ROLE_USER");
                return Optional.of(roleRepo.save(r));
            });

            roleRepo.findByName("ROLE_ADMIN").or(() -> {
                Role r = new Role();
                r.setName("ROLE_ADMIN");
                return Optional.of(roleRepo.save(r));
            });

            if(!authRepo.findByUsername("admin").isPresent()) {
                Role adminRole = roleRepo.findByName("ROLE_ADMIN").get();
                AuthUser admin = new AuthUser();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin@123"));
                admin.setEmail("admin@dummy.com");
                admin.setRoles(Set.of(adminRole));
                authRepo.save(admin);
            }

            if (userRepo.findByUsername("admin") == null) {
                    User u = new User();
                    u.setUsername("admin");
                    u.setEmail("admin@dummy.com");
                    UserProfile p = new UserProfile();
                    p.setFullName("Administrator");
                    p.setPhone("NA");
                    p.setAddress("NA");
                    p.setUser(u);
                    u.setProfile(p);
                    userRepo.save(u);
                }
        };
    }
}
