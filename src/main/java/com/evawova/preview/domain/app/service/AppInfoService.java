package com.evawova.preview.domain.app.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evawova.preview.domain.app.entity.AppInfo;
import com.evawova.preview.domain.app.repository.AppInfoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AppInfoService {

    private final AppInfoRepository appInfoRepository;
    
    /**
     * 최신 앱 정보 조회
     */
    @Transactional(readOnly = true)
    public AppInfo getLatestAppInfo() {
        return appInfoRepository.findTopByOrderByLastUpdatedAtDesc();
    }
    
    /**
     * 앱 정보 생성
     */
    public AppInfo createAppInfo(String appName, String appVersion, String appDescription, 
                               String logoUrl, String termsOfService, String privacyPolicy,
                               String companyName, String contactEmail) {
        
        AppInfo appInfo = AppInfo.builder()
                .appName(appName)
                .appVersion(appVersion)
                .appDescription(appDescription)
                .logoUrl(logoUrl)
                .termsOfService(termsOfService)
                .privacyPolicy(privacyPolicy)
                .companyName(companyName)
                .contactEmail(contactEmail)
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
        
        // FAQ 내용 설정
        appInfo.setFaqContent(faqContent);
        
        // 지원 언어 추가
        appInfo.getSupportedLanguages().addAll(Set.of("ko", "en", "ja"));
        
        // 소셜 미디어 링크 추가
        appInfo.getSocialMediaLinks().put("instagram", "https://instagram.com/evawova");
        appInfo.getSocialMediaLinks().put("twitter", "https://twitter.com/evawova");
        appInfo.getSocialMediaLinks().put("facebook", "https://facebook.com/evawova");
        
        // 공지사항 추가
        appInfo.getNotices().add("서비스 오픈 안내");
        appInfo.getNotices().add("이벤트 진행 중");
        
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
            "https://example.com/logo.png",
            "서비스 이용약관 v1.0", 
            "개인정보처리방침 v1.0",
            "Evawova", 
            "contact@evawova.com");
        
        // 두 번째 버전
        AppInfo appInfo2 = createAppInfo(
            "Preview Service", 
            "1.1.0", 
            "AI 기반 콘텐츠 미리보기 서비스 - 업데이트 버전",
            "https://example.com/logo_v2.png",
            "서비스 이용약관 v1.1", 
            "개인정보처리방침 v1.1",
            "Evawova Inc.", 
            "support@evawova.com");
        
        // 두 번째 버전의 추가 정보 업데이트
        appInfo2.updateLegalInfo(
            appInfo2.getTermsOfService(),
            appInfo2.getPrivacyPolicy(),
            "MIT License",
            "Copyright (c) 2023 Evawova Inc."
        );
        
        appInfo2.updateCompanyInfo(
            appInfo2.getCompanyName(),
            "123-45-67890",
            "홍길동",
            "서울시 강남구 테헤란로 123",
            appInfo2.getContactEmail(),
            "02-123-4567"
        );
        
        // 배포 정보 업데이트
        appInfo2.updateDeploymentInfo(
            "자동 배포 시스템을 통한 배포",
            "DEPLOYED",
            "성능 개선 및 버그 수정"
        );
        
        // 다국어 약관 추가
        appInfo2.getTermsOfServiceTranslations().put("en", "Terms of Service in English");
        appInfo2.getTermsOfServiceTranslations().put("ja", "利用規約");
        
        return appInfoRepository.saveAll(List.of(appInfo1, appInfo2));
    }
    
    /**
     * 배포 정보 업데이트
     */
    public AppInfo updateDeploymentInfo(Long appInfoId, String deploymentInfo, 
                                      String deploymentStatus, String deploymentNotes) {
        AppInfo appInfo = appInfoRepository.findById(appInfoId)
                .orElseThrow(() -> new IllegalArgumentException("앱 정보를 찾을 수 없습니다."));
        
        appInfo.updateDeploymentInfo(deploymentInfo, deploymentStatus, deploymentNotes);
        return appInfoRepository.save(appInfo);
    }
} 