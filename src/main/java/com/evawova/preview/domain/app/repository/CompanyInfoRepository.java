package com.evawova.preview.domain.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evawova.preview.domain.app.entity.CompanyInfo;

public interface CompanyInfoRepository extends JpaRepository<CompanyInfo, Long> {
    CompanyInfo findByAppInfoId(Long appInfoId);
} 