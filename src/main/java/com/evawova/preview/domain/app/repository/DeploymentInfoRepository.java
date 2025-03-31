package com.evawova.preview.domain.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evawova.preview.domain.app.entity.DeploymentInfo;

public interface DeploymentInfoRepository extends JpaRepository<DeploymentInfo, Long> {
    DeploymentInfo findByAppInfoId(Long appInfoId);
} 