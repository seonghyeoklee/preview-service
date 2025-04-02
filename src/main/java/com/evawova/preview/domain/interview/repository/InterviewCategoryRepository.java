package com.evawova.preview.domain.interview.repository;

import com.evawova.preview.domain.interview.entity.InterviewCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewCategoryRepository extends JpaRepository<InterviewCategory, Long> {
}