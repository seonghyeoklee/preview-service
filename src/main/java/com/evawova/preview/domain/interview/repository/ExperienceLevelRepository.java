package com.evawova.preview.domain.interview.repository;

import com.evawova.preview.domain.interview.entity.ExperienceLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExperienceLevelRepository extends JpaRepository<ExperienceLevel, Long> {

}