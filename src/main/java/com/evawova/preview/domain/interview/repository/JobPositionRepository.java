package com.evawova.preview.domain.interview.repository;

import com.evawova.preview.domain.interview.entity.JobPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobPositionRepository extends JpaRepository<JobPosition, Long> {

    /**
     * 코드로 직무 조회
     */
    Optional<JobPosition> findByCode(String code);

    /**
     * 직군 ID로 직무 목록 조회
     */
    List<JobPosition> findByJobField_Id(Long jobFieldId);

    /**
     * 활성화된 직무만 조회
     */
    List<JobPosition> findByActiveTrue();

    /**
     * 정렬 순서대로 직무 조회
     */
    List<JobPosition> findAllByOrderBySortOrderAsc();

    /**
     * 직군 ID별로 직무 조회 (정렬 순서대로)
     */
    List<JobPosition> findByJobField_IdOrderBySortOrderAsc(Long jobFieldId);

    /**
     * 활성화된 직무를 직군 ID별로 조회 (정렬 순서대로)
     */
    List<JobPosition> findByJobField_IdAndActiveTrueOrderBySortOrderAsc(Long jobFieldId);
}