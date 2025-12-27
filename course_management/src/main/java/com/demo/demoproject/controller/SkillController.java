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
import com.demo.demoproject.modal.Skills;
import com.demo.demoproject.repo.UserRepository;
import com.demo.demoproject.service.SkillService;
import com.demo.demoproject.util.SecurityUtils;

@RestController
@RequestMapping("/skills")
public class SkillController {

    @Autowired
    private SkillService skillService;

    @Autowired
    private UserRepository userRepo;

    // CREATE Skill
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Skills> createSkill(@RequestBody Skills skill) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.createSkill(skill));
    }

    // GET All Skills
    @GetMapping
    public ResponseEntity<List<Skills>> getAllSkills() {
        if (SecurityUtils.currentUserIsAdmin()) {
            return ResponseEntity.ok(skillService.getAllSkills());
        }
        var current = SecurityUtils.getCurrentUser(userRepo);
        if(current == null) throw new UnauthorizedException("Authentication required");
        List<Skills> userSkills = current.getSkills();
        return ResponseEntity.ok(userSkills);
    }

    // UPDATE Skill
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{skillId}")
    public ResponseEntity<Skills> updateSkill(
            @PathVariable Long skillId,
            @RequestBody Skills skill) {
        Skills updated = skillService.updateSkill(skillId, skill);
        return ResponseEntity.ok(updated);
    }

    // DELETE Skill
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.ok("Skill deleted successfully");
    }
}
