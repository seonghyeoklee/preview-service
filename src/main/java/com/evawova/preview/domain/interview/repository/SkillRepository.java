package com.evawova.preview.domain.interview.repository;

import com.evawova.preview.domain.interview.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByNameEn(String nameEn);
}