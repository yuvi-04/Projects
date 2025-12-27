package com.demo.demoproject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.demoproject.exception.ResourceNotFoundException;
import com.demo.demoproject.exception.SaveFailureException;
import com.demo.demoproject.modal.Skills;
import com.demo.demoproject.repo.SkillRepository;

@Service
public class SkillService {

    @Autowired
    private SkillRepository skillsRepo;

    // CREATE skill
    public Skills createSkill(Skills skill) {
        try {
            return skillsRepo.save(skill);
        } catch (Exception ex) {
            throw new SaveFailureException("Failed to create skill", ex);
        }
    }

    // GET all skills
    public List<Skills> getAllSkills() {
        return skillsRepo.findAll();
    }

    // UPDATE skill
    public Skills updateSkill(Long skillId, Skills updatedSkill) {
        Skills existing = skillsRepo.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + skillId));

        if (updatedSkill.getName() != null) existing.setName(updatedSkill.getName());

        try {
            return skillsRepo.save(existing);
        } catch (Exception ex) {
            throw new SaveFailureException("Failed to update skill", ex);
        }
    }

    // DELETE skill
    public boolean deleteSkill(Long skillId) {
        if (!skillsRepo.existsById(skillId)) {
            throw new ResourceNotFoundException("Skill not found: " + skillId);
        }

        skillsRepo.deleteById(skillId);
        return true;
    }
}
