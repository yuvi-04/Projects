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
import com.demo.demoproject.repo.UserRepository;
import com.demo.demoproject.service.CourseService;
import com.demo.demoproject.util.SecurityUtils;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserRepository userRepo;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(course));
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        if (SecurityUtils.currentUserIsAdmin()) {
            return ResponseEntity.ok(courseService.getAllCourses());
        }
        var current = SecurityUtils.getCurrentUser(userRepo);
        if (current == null) throw new UnauthorizedException("Authentication required");
        List<Course> userCourses = current.getCourses();
        return ResponseEntity.ok(userCourses);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{courseId}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Long courseId,
            @RequestBody Course course) {
        Course updated = courseService.updateCourse(courseId, course);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok("Course deleted successfully");
    }
}
