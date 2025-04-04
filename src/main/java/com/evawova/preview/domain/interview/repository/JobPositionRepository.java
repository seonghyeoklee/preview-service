package com.evawova.preview.domain.interview.repository;

import com.evawova.preview.domain.interview.entity.JobPosition;
import com.evawova.preview.domain.interview.model.InterviewType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobPositionRepository extends JpaRepository<JobPosition, Long> {

    // Eagerly fetch skills to avoid N+1 problem in the service layer when
    // converting to DTO
    @Query("SELECT jp FROM JobPosition jp LEFT JOIN FETCH jp.skills")
    List<JobPosition> findAllWithSkills();

    @Query("SELECT jp FROM JobPosition jp LEFT JOIN FETCH jp.skills WHERE jp.category.id = :categoryId")
    List<JobPosition> findAllByCategoryIdWithSkills(@Param("categoryId") Long categoryId);

    @Query("SELECT jp FROM JobPosition jp LEFT JOIN FETCH jp.skills WHERE jp.category.type = :categoryType")
    List<JobPosition> findAllByCategoryTypeWithSkills(@Param("categoryType") InterviewType categoryType);
}