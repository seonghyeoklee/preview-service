package com.evawova.preview.domain.interview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.evawova.preview.domain.interview.entity.InterviewPrompt;

import java.util.List;

@Repository
public interface InterviewPromptRepository extends JpaRepository<InterviewPrompt, Long> {
    List<InterviewPrompt> findByCategoryAndActive(String category, boolean active);
    List<InterviewPrompt> findByActive(boolean active);
} 