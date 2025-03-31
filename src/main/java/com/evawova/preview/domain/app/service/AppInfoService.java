package com.evawova.preview.domain.app.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evawova.preview.domain.app.entity.AppInfo;
import com.evawova.preview.domain.app.entity.CompanyInfo;
import com.evawova.preview.domain.app.entity.DeploymentInfo;
import com.evawova.preview.domain.app.entity.LegalInfo;
import com.evawova.preview.domain.app.entity.ServiceStatus;
import com.evawova.preview.domain.app.entity.ServiceStatusInfo;
import com.evawova.preview.domain.app.repository.AppInfoRepository;
import com.evawova.preview.domain.app.repository.CompanyInfoRepository;
import com.evawova.preview.domain.app.repository.DeploymentInfoRepository;
import com.evawova.preview.domain.app.repository.LegalInfoRepository;
import com.evawova.preview.domain.app.repository.ServiceStatusInfoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AppInfoService {

    private final AppInfoRepository appInfoRepository;
    private final LegalInfoRepository legalInfoRepository;
    private final CompanyInfoRepository companyInfoRepository;
    private final ServiceStatusInfoRepository serviceStatusInfoRepository;
    private final DeploymentInfoRepository deploymentInfoRepository;
    
    /**
     * 최신 앱 정보 조회
     */
    @Transactional(readOnly = true)
    public AppInfo getLatestAppInfo() {
        return appInfoRepository.findTopByOrderByUpdatedAtDesc();
    }
    
    /**
     * 앱 정보 생성
     */
    public AppInfo createAppInfo(String appName, String appVersion, String appDescription, String logoUrl) {
        // 기본 앱 정보 생성
        AppInfo appInfo = AppInfo.builder()
                .appName(appName)
                .appVersion(appVersion)
                .appDescription(appDescription)
                .logoUrl(logoUrl)
                .build();
        
        appInfo = appInfoRepository.save(appInfo);
        
        // 법적 정보 생성
        LegalInfo legalInfo = LegalInfo.builder()
                .appInfo(appInfo)
                .termsOfService("서비스 이용약관")
                .privacyPolicy("개인정보처리방침")
                .build();
        legalInfo = legalInfoRepository.save(legalInfo);
        appInfo.setLegalInfo(legalInfo);
        
        // 회사 정보 생성
        CompanyInfo companyInfo = CompanyInfo.builder()
                .appInfo(appInfo)
                .companyName("Evawova")
                .contactEmail("contact@evawova.com")
                .build();
        companyInfo = companyInfoRepository.save(companyInfo);
        appInfo.setCompanyInfo(companyInfo);
        
        // 서비스 상태 정보 생성
        ServiceStatusInfo statusInfo = ServiceStatusInfo.builder()
                .appInfo(appInfo)
                .build();
        
        // FAQ 내용 초기화
        Map<String, Object> faqContent = new HashMap<>();
        faqContent.put("일반", Map.of(
            "앱은 어떻게 사용하나요?", "앱 사용 방법은 매뉴얼을 참고해주세요.",
            "비밀번호를 잊어버렸어요.", "로그인 화면에서 '비밀번호 찾기'를 이용해주세요."
        ));
        faqContent.put("결제", Map.of(
            "결제 방법은 어떤 것이 있나요?", "신용카드, 계좌이체, 페이팔 등을 지원합니다.",
            "환불은 어떻게 하나요?", "구매 후 7일 이내에는 전액 환불 가능합니다."
        ));
        
        // FAQ 설정
        statusInfo.setFaqContent(faqContent);
        
        // 지원 언어 추가
        statusInfo.getSupportedLanguages().addAll(Set.of("ko", "en", "ja"));
        
        // 공지사항 추가
        statusInfo.getNotices().add("서비스 오픈 안내");
        statusInfo.getNotices().add("이벤트 진행 중");
        
        statusInfo = serviceStatusInfoRepository.save(statusInfo);
        appInfo.setServiceStatusInfo(statusInfo);
        
        // 배포 정보 생성
        DeploymentInfo deploymentInfo = DeploymentInfo.builder()
                .appInfo(appInfo)
                .build();
        deploymentInfo = deploymentInfoRepository.save(deploymentInfo);
        appInfo.setDeploymentInfo(deploymentInfo);
        
        return appInfoRepository.save(appInfo);
    }
    
    /**
     * 초기 앱 정보 여러 개 생성 (테스트용)
     */
    public List<AppInfo> createSampleAppInfos() {
        // 첫 번째 버전
        AppInfo appInfo1 = createAppInfo(
            "Preview Service", 
            "1.0.0", 
            "AI 기반 콘텐츠 미리보기 서비스",
            "https://example.com/logo.png");
        
        // 두 번째 버전
        AppInfo appInfo2 = createAppInfo(
            "Preview Service", 
            "1.1.0", 
            "AI 기반 콘텐츠 미리보기 서비스 - 업데이트 버전",
            "https://example.com/logo_v2.png");
        
        // 두 번째 버전의 법적 정보 업데이트
        LegalInfo legalInfo = appInfo2.getLegalInfo();
        legalInfo.updateLegalInfo(
            "서비스 이용약관 v1.1",
            "개인정보처리방침 v1.1",
            "MIT License",
            "Copyright (c) 2023 Evawova Inc."
        );
        legalInfo.getTermsOfServiceTranslations().put("en", "Terms of Service in English");
        legalInfo.getTermsOfServiceTranslations().put("ja", "利用規約");
        legalInfoRepository.save(legalInfo);
        
        // 두 번째 버전의 회사 정보 업데이트
        CompanyInfo companyInfo = appInfo2.getCompanyInfo();
        companyInfo.updateCompanyInfo(
            "Evawova Inc.",
            "123-45-67890",
            "홍길동",
            "서울시 강남구 테헤란로 123",
            "support@evawova.com",
            "02-123-4567"
        );
        companyInfoRepository.save(companyInfo);
        
        // 두 번째 버전의 배포 정보 업데이트
        DeploymentInfo deploymentInfo = appInfo2.getDeploymentInfo();
        deploymentInfo.updateDeploymentInfo(
            "자동 배포 시스템을 통한 배포",
            "DEPLOYED",
            "성능 개선 및 버그 수정"
        );
        deploymentInfoRepository.save(deploymentInfo);
        
        return List.of(appInfo1, appInfo2);
    }
    
    /**
     * 서비스 상태 업데이트
     */
    public ServiceStatusInfo updateServiceStatus(Long appInfoId, ServiceStatus status, String message) {
        ServiceStatusInfo statusInfo = serviceStatusInfoRepository.findByAppInfoId(appInfoId);
        if (statusInfo == null) {
            throw new IllegalArgumentException("서비스 상태 정보를 찾을 수 없습니다.");
        }
        
        statusInfo.updateServiceStatus(status, message);
        return serviceStatusInfoRepository.save(statusInfo);
    }
    
    /**
     * 긴급 공지 설정
     */
    public ServiceStatusInfo setEmergencyNotice(Long appInfoId, String notice, 
                                             LocalDateTime startAt, LocalDateTime endAt) {
        ServiceStatusInfo statusInfo = serviceStatusInfoRepository.findByAppInfoId(appInfoId);
        if (statusInfo == null) {
            throw new IllegalArgumentException("서비스 상태 정보를 찾을 수 없습니다.");
        }
        
        statusInfo.setEmergencyNotice(notice, startAt, endAt);
        return serviceStatusInfoRepository.save(statusInfo);
    }
    
    /**
     * 긴급 공지 해제
     */
    public ServiceStatusInfo clearEmergencyNotice(Long appInfoId) {
        ServiceStatusInfo statusInfo = serviceStatusInfoRepository.findByAppInfoId(appInfoId);
        if (statusInfo == null) {
            throw new IllegalArgumentException("서비스 상태 정보를 찾을 수 없습니다.");
        }
        
        statusInfo.clearEmergencyNotice();
        return serviceStatusInfoRepository.save(statusInfo);
    }
    
    /**
     * 배포 정보 업데이트
     */
    public DeploymentInfo updateDeploymentInfo(Long appInfoId, String deploymentInfo, 
                                           String deploymentStatus, String deploymentNotes) {
        DeploymentInfo deployInfo = deploymentInfoRepository.findByAppInfoId(appInfoId);
        if (deployInfo == null) {
            throw new IllegalArgumentException("배포 정보를 찾을 수 없습니다.");
        }
        
        deployInfo.updateDeploymentInfo(deploymentInfo, deploymentStatus, deploymentNotes);
        return deploymentInfoRepository.save(deployInfo);
    }
    
    /**
     * 긴급 공지 상태 자동 업데이트 (스케줄러에서 호출)
     */
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void updateEmergencyNoticeStatus() {
        AppInfo appInfo = getLatestAppInfo();
        if (appInfo != null && appInfo.getServiceStatusInfo() != null) {
            ServiceStatusInfo statusInfo = appInfo.getServiceStatusInfo();
            statusInfo.updateEmergencyNoticeStatus();
            serviceStatusInfoRepository.save(statusInfo);
        }
    }
} 