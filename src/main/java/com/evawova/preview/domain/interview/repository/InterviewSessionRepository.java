package com.evawova.preview.domain.interview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evawova.preview.domain.interview.entity.InterviewSession;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {

}
