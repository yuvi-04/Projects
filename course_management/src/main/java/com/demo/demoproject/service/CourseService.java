package com.demo.demoproject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.demoproject.exception.ResourceNotFoundException;
import com.demo.demoproject.exception.SaveFailureException;
import com.demo.demoproject.modal.Course;
import com.demo.demoproject.modal.User;
import com.demo.demoproject.repo.CourseRepository;
import com.demo.demoproject.repo.UserRepository;

@Service
@Transactional
public class CourseService {
    @Autowired
    CourseRepository courseRepo;

    @Autowired
    private UserRepository userRepo;

    public Course createCourse(Course course) {
        try {
            if (course.getInstructor() != null && course.getInstructor().getId() != null) {
                User instructor = userRepo.findById(course.getInstructor().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Instructor not found: " + course.getInstructor().getId()));
                course.setInstructor(instructor);
                
                if (instructor.getCourses() != null) instructor.getCourses().add(course);
            } else {
                throw new SaveFailureException("Instructor must be provided for Course", null);
            }
            return courseRepo.save(course);
        } catch(Exception ex) {
            throw new SaveFailureException("Failed to save Course", ex);
        }
    }

    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

    public Course updateCourse(Long courseId, Course course) {
        Course existing = courseRepo.findById(courseId).orElseThrow(
            () -> new ResourceNotFoundException("Course not Found" + courseId)
        );

        if(course.getTitle() != null) existing.setTitle(course.getTitle());
        if(course.getPrice() != 0) existing.setPrice(course.getPrice());

        if (course.getInstructor() != null && course.getInstructor().getId() != null) {
            Long newInstructorId = course.getInstructor().getId();
            User newInstructor = userRepo.findById(newInstructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found: " + newInstructorId));
            User oldInstructor = existing.getInstructor();
            if (oldInstructor == null || !oldInstructor.getId().equals(newInstructorId)) {
                if (oldInstructor != null && oldInstructor.getCourses() != null) {
                    oldInstructor.getCourses().remove(existing);
                }
                if (newInstructor.getCourses() != null) newInstructor.getCourses().add(existing);
                existing.setInstructor(newInstructor);
            }
        }

        try {
            return courseRepo.save(existing);
        } catch(Exception ex) {
            throw new SaveFailureException("Failed to update Course", ex);
        }
    }

    public boolean deleteCourse(Long courseId) {
        if(!courseRepo.existsById(courseId))
            throw new ResourceNotFoundException("Course not found: " + courseId);
        courseRepo.deleteById(courseId);
        return true;
    }
}
