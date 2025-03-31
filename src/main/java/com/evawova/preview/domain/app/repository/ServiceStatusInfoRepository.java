package com.evawova.preview.domain.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evawova.preview.domain.app.entity.ServiceStatusInfo;

public interface ServiceStatusInfoRepository extends JpaRepository<ServiceStatusInfo, Long> {
    ServiceStatusInfo findByAppInfoId(Long appInfoId);
} 