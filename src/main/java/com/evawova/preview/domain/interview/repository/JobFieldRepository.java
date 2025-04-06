package com.evawova.preview.domain.interview.repository;

import com.evawova.preview.domain.interview.entity.JobField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobFieldRepository extends JpaRepository<JobField, Long> {

    /**
     * 코드로 직군 조회
     */
    Optional<JobField> findByCode(String code);

    /**
     * 활성화된 직군만 조회
     */
    List<JobField> findByActiveTrue();

    /**
     * 정렬 순서대로 직군 조회
     */
    List<JobField> findAllByOrderBySortOrderAsc();

    /**
     * 활성화된 직군을 정렬 순서대로 조회
     */
    List<JobField> findByActiveTrueOrderBySortOrderAsc();
}