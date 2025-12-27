package com.demo.demoproject.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.demoproject.modal.Skills;
import java.util.Optional;


@Repository
public interface SkillRepository extends JpaRepository<Skills, Long> {
    Optional<Skills> findByName(String name);
}
