package com.evawova.preview.domain.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evawova.preview.domain.app.entity.LegalInfo;

public interface LegalInfoRepository extends JpaRepository<LegalInfo, Long> {
    LegalInfo findByAppInfoId(Long appInfoId);
} 