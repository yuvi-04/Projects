package com.demo.demoproject.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.demo.demoproject.modal.Course;

import jakarta.transaction.Transactional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Course c WHERE c.title = :title")
    int deleteCourseByTitle(@Param("title") String title);

    @Query("SELECT MAX(c.price) FROM Course c")
    Double findMaxCoursePrice();

    @Query("SELECT AVG(c.price) FROM Course c")
    Double findAverageCoursePrice();

    @Query("SELECT c FROM Course c ORDER BY c.price DESC")
    List<Course> findCoursesOrderByPriceDesc();

    @Query("SELECT c FROM Course c ORDER BY c.price DESC")
    List<Course> findTopCourses(Pageable pageable);
}
