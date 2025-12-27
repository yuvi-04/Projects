package com.demo.demoproject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.demoproject.exception.UnauthorizedException;
import com.demo.demoproject.modal.Course;
import com.demo.demoproject.modal.Skills;
import com.demo.demoproject.modal.User;
import com.demo.demoproject.modal.UserProfile;
import com.demo.demoproject.repo.UserRepository;
import com.demo.demoproject.service.UserService;
import com.demo.demoproject.util.SecurityUtils;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    // Create User with Profile + Courses
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(userService.createUserWithProfile(user));
    }

    // Get User by ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(id));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMe() {
        var current = SecurityUtils.getCurrentUser(userRepo);
        if(current == null) throw new UnauthorizedException("Authentication required");
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(current.getId()));
    }

    // Update User Profile
    @PutMapping("/me/profile")
    public ResponseEntity<User> updateUserProfile(@RequestBody UserProfile profile) {
        var current = SecurityUtils.getCurrentUser(userRepo);
        if(current == null) throw new UnauthorizedException("Authentication required");
        userService.updateProfile(current.getId(), profile);
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(current.getId()));
    }

    // Add Course for User
    @PostMapping("/me/courses")
    public ResponseEntity<Course> addCourse(@RequestBody Course course) {
        var current = SecurityUtils.getCurrentUser(userRepo);
        if(current == null) throw new UnauthorizedException("Authentication required");
        Course saved = userService.addCourse(current.getId(), course);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Update Course for User
    @PutMapping("/me/courses/{courseId}")
    public ResponseEntity<Course> updateCourse(
        @PathVariable Long courseId,
        @RequestBody Course course
    ) {
        var current = SecurityUtils.getCurrentUser(userRepo);
        if(current == null) throw new UnauthorizedException("Authentication required");
        Course updated = userService.updateCourse(current.getId(), courseId, course);
        return ResponseEntity.ok(updated);
    }

    // Delete Course for User
    // user + profile remains
    @DeleteMapping("/me/courses/{courseId}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long courseId) {
        var current = SecurityUtils.getCurrentUser(userRepo);
        if(current == null) throw new UnauthorizedException("Authentication required");
        userService.deleteCourse(current.getId(), courseId);
        return ResponseEntity.ok("Course deleted successfully");
    }

    // Delete User along with Profile and Courses
    // deleting the parent User will cascade delete profile and courses
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully.");
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMyAccount() {
        var current = SecurityUtils.getCurrentUser(userRepo);
        if(current == null) throw new UnauthorizedException("Authentication required");
        userService.deleteUser(current.getId());
        return ResponseEntity.ok("Your account deleted");
    }

    @PostMapping("/me/skills/{skillId}")
    public ResponseEntity<User> addSkill(@PathVariable Long skillId) {
        var current = SecurityUtils.getCurrentUser(userRepo);
        if(current == null) throw new UnauthorizedException("Authentication required");
        User user = userService.addSkillToUser(current.getId(), skillId);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/me/skills/{skillId}")
    public ResponseEntity<User> removeSkill(@PathVariable Long skillId) {
        var current = SecurityUtils.getCurrentUser(userRepo);
        if(current == null) throw new UnauthorizedException("Authentication required");
        User user = userService.removeSkillFromUser(current.getId(), skillId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me/skills")
    public ResponseEntity<List<Skills>> getUserSkills() {
        var current = SecurityUtils.getCurrentUser(userRepo);
        if(current == null) throw new UnauthorizedException("Authentication required");
        List<Skills> skills = userService.getUserSkills(current.getId());
        return ResponseEntity.ok(skills);
    }
}