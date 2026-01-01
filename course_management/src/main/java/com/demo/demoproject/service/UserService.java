package com.demo.demoproject.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.demo.demoproject.exception.ResourceNotFoundException;
import com.demo.demoproject.exception.UnauthorizedException;
import com.demo.demoproject.exception.SaveFailureException;
import com.demo.demoproject.modal.Course;
import com.demo.demoproject.modal.Skills;
import com.demo.demoproject.modal.User;
import com.demo.demoproject.modal.UserProfile;
import com.demo.demoproject.repo.CourseRepository;
import com.demo.demoproject.repo.SkillRepository;
import com.demo.demoproject.repo.UserProfileRepository;
import com.demo.demoproject.repo.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserProfileRepository profileRepo;

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private SkillRepository skillRepo;

    // Create User with Profile + Courses
    public User createUserWithProfile(User user) {
        if (user.getProfile() != null)
            user.getProfile().setUser(user);

        if (user.getCourses() != null) {
            user.getCourses().forEach(course -> course.setInstructor(user));
        }

        try {
            return userRepo.save(user);
        } catch (Exception ex) {
            throw new SaveFailureException("Failed to save user", ex);
        }
    }

    // Get User by ID
    @Cacheable(value = "users", key = "#userId")
    public User getUser(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    // Add Course for User
    @CacheEvict(value = {"users", "courses"}, key = "#userId", allEntries = true)
    public Course addCourse(Long userId, Course course) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        course.setInstructor(user);
        try {
            return courseRepo.save(course);
        } catch (Exception ex) {
            throw new SaveFailureException("Failed to save course", ex);
        }
    }

    // Update Course for User
    @CacheEvict(value = {"users", "courses"}, key = "#userId", allEntries = true)
    public Course updateCourse(Long userId, Long courseId, Course newCourse) {
        Course existingCourse = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

        if (!existingCourse.getInstructor().getId().equals(userId)) {
            throw new UnauthorizedException("Course does not belong to this user");
        }

        if (newCourse.getTitle() != null) existingCourse.setTitle(newCourse.getTitle());
        if (newCourse.getPrice() != 0) existingCourse.setPrice(newCourse.getPrice());

        try {
            return courseRepo.save(existingCourse);
        } catch (Exception ex) {
            throw new SaveFailureException("Failed to update course", ex);
        }
    }

    // Delete Course for User
    @CacheEvict(value = {"users", "courses"}, key = "#userId", allEntries = true)
    public boolean deleteCourse(Long userId, Long courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

        if (!course.getInstructor().getId().equals(userId)) {
            throw new UnauthorizedException("Unauthorized delete");
        }

        courseRepo.delete(course);
        return true;
    }

    // Update User Profile
    @CacheEvict(value = "users", key = "#userId")
    public UserProfile updateProfile(Long userId, UserProfile profile) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        UserProfile existingProfile = user.getProfile();
        if (existingProfile == null) {
            throw new ResourceNotFoundException("Profile not found for user: " + userId);
        }

        if (profile.getAddress() != null) existingProfile.setAddress(profile.getAddress());
        if (profile.getPhone() != null) existingProfile.setPhone(profile.getPhone());
        if (profile.getFullName() != null) existingProfile.setFullName(profile.getFullName());

        try {
            return profileRepo.save(existingProfile);
        } catch (Exception ex) {
            throw new SaveFailureException("Failed to update profile", ex);
        }
    }

    // Delete User along with Profile and Courses
    @CacheEvict(value = "users", key = "#userId")
    public boolean deleteUser(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        userRepo.deleteById(userId);
        return true;
    }

    @CacheEvict(value = "users", key = "#userId")
    public User addSkillToUser(Long userId, Long skillId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Skills skill = skillRepo.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + skillId));

        if (user.getSkills() == null) user.setSkills(new ArrayList<>());
        user.getSkills().add(skill);
        try {
            return userRepo.save(user);
        } catch (Exception ex) {
            throw new SaveFailureException("Failed to add skill to user", ex);
        }
    }

    @CacheEvict(value = "users", key = "#userId")
    public User removeSkillFromUser(Long userId, Long skillId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        boolean removed = user.getSkills() != null && user.getSkills().removeIf(s -> s.getId().equals(skillId));
        if (!removed) {
            throw new ResourceNotFoundException("Skill not associated with user: " + skillId);
        }

        try {
            return userRepo.save(user);
        } catch (Exception ex) {
            throw new SaveFailureException("Failed to remove skill from user", ex);
        }
    }

    @Cacheable(value = "userSkills", key = "#userId")
    public List<Skills> getUserSkills(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        return user.getSkills() == null ? Collections.emptyList() : user.getSkills();
    }
}
