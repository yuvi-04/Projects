package com.demo.demoproject.controller;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.demo.demoproject.modal.User;
import com.demo.demoproject.modal.UserProfile;
import com.demo.demoproject.repo.AuthUserRepository;
import com.demo.demoproject.repo.RoleRepository;
import com.demo.demoproject.repo.UserRepository;
import com.demo.demoproject.security.jwt.JwtUtil;
import com.demo.demoproject.security.model.AuthUser;
import com.demo.demoproject.security.model.Role;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private AuthUserRepository authUserRepo;
    @Autowired private RoleRepository roleRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UserRepository userRepo;

    record LoginRequest(String username, String password) {}
    record LoginResponse(String token) {}

    public ResponseEntity<LoginResponse> loginRateLimiter(
        LoginRequest req, HttpServletRequest request, RequestNotPermitted ex) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new LoginResponse("Too many Login Attempts"));
    }

    @RateLimiter(name = "loginLimiter", fallbackMethod = "loginRateLimiter")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(req.username(), req.password());
        authToken.setDetails(new WebAuthenticationDetails(request));
        authManager.authenticate(authToken);

        AuthUser au = authUserRepo.findByUsername(req.username()).orElseThrow();
        var roles = au.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        String token = jwtUtil.generatetoken(au.getUsername(), roles);
        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(token));
    }

    record SignupRequest(String username, String password, String email, String fullName, String phone, String address) {}

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest req) {
        if(authUserRepo.existsByUsername(req.username()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("username already exist");

        Role userRole = roleRepo.findByName("ROLE_USER").orElseThrow();
        AuthUser au = new AuthUser();
        au.setUsername(req.username());
        au.setPassword(passwordEncoder.encode(req.password()));
        au.setEmail(req.email());
        au.setRoles(Set.of(userRole));
        authUserRepo.save(au);

        User existing = userRepo.findByUsername(req.username());
        if(existing == null) {
            User u = new User();
            u.setUsername(req.username());
            u.setEmail(req.email() == null ? req.username() : req.email());
            UserProfile p = new UserProfile();
            p.setFullName(req.fullName() == null ? "" : req.fullName());
            p.setPhone(req.phone() == null ? "" : req.phone());
            p.setAddress(req.address() == null ? "" : req.address());
            p.setUser(u);
            u.setProfile(p);
            userRepo.save(u);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Successfull");
    }
}
