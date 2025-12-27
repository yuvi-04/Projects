package com.demo.demoproject.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.demo.demoproject.modal.Course;
import com.demo.demoproject.modal.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.username = :username")
    User findByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.email = :email WHERE u.id = :id")
    int updateUserEmail(@Param("id") Long id, @Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    List<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.username LIKE :pattern")
    List<User> findUsersByUsernameLike(@Param("pattern") String pattern);

    @Query("""
    SELECT u.username, COUNT(c)
    FROM User u JOIN u.courses c
    GROUP BY u.username
    """)
    List<Object[]> countCoursesPerUser();

    // Inner Join
    @Query("SELECT u FROM User u JOIN u.courses c")
    List<User> findUsersWithCourses();

    // Left Join
    @Query("SELECT u FROM User u LEFT JOIN u.courses c")
    List<User> findAllUsersWithOrWithoutCourses();

    // Right Join
    @Query("SELECT c FROM User u RIGHT JOIN u.courses c")
    List<Course> rightJoinExample();

    // Cross Join
    @Query("SELECT u.username, s.name FROM User u, Skills s")
    List<Object[]> crossJoinUsersSkills();

    //Many-to-Many Join
    @Query("""
    SELECT u FROM User u
    JOIN u.skills s
    WHERE s.name = :skillName
    """)
    List<User> findUsersBySkillName(@Param("skillName") String skillName);

    // Fetch Join
    @Query("SELECT u FROM User u JOIN FETCH u.skills")
    List<User> fetchUsersWithSkills();

    //one on one join user and profile
    @Query("""
    SELECT u.username, p.fullName
    FROM User u JOIN u.profile p
    """)
    List<Object[]> fetchUsernameAndFullName();
}
