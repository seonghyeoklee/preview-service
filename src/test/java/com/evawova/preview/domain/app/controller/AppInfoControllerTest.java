package com.evawova.preview.domain.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.evawova.preview.config.TestSecurityConfig;
import com.evawova.preview.domain.app.entity.AppInfo;
import com.evawova.preview.domain.app.entity.CompanyInfo;
import com.evawova.preview.domain.app.entity.DeploymentInfo;
import com.evawova.preview.domain.app.entity.LegalInfo;
import com.evawova.preview.domain.app.entity.ServiceStatus;
import com.evawova.preview.domain.app.entity.ServiceStatusInfo;
import com.evawova.preview.domain.app.service.AppInfoService;

@WebMvcTest(AppInfoController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class AppInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppInfoService appInfoService;

    private AppInfo mockAppInfo;
    private LegalInfo mockLegalInfo;
    private CompanyInfo mockCompanyInfo;
    private ServiceStatusInfo mockServiceStatusInfo;
    private DeploymentInfo mockDeploymentInfo;

    @BeforeEach
    void setUp() {
        // AppInfo 설정
        mockAppInfo = AppInfo.builder()
                .id(1L)
                .appName("Preview Service")
                .appVersion("1.1.0")
                .appDescription("AI 기반 콘텐츠 미리보기 서비스")
                .logoUrl("https://example.com/logo.png")
                .build();

        // LegalInfo 설정
        mockLegalInfo = LegalInfo.builder()
                .id(1L)
                .appInfo(mockAppInfo)
                .termsOfService("서비스 이용약관 v1.1")
                .privacyPolicy("개인정보처리방침 v1.1")
                .licenseInfo("MIT License")
                .copyrightInfo("Copyright (c) 2023 Evawova Inc.")
                .termsOfServiceTranslations(Map.of(
                    "en", "Terms of Service in English",
                    "ja", "利用規約"
                ))
                .build();
        mockAppInfo.setLegalInfo(mockLegalInfo);

        // CompanyInfo 설정
        mockCompanyInfo = CompanyInfo.builder()
                .id(1L)
                .appInfo(mockAppInfo)
                .companyName("Evawova Inc.")
                .businessRegistrationNumber("123-45-67890")
                .representativeName("홍길동")
                .address("서울시 강남구 테헤란로 123")
                .contactEmail("support@evawova.com")
                .contactPhone("02-123-4567")
                .build();
        mockAppInfo.setCompanyInfo(mockCompanyInfo);

        // ServiceStatusInfo 설정
        mockServiceStatusInfo = ServiceStatusInfo.builder()
                .id(1L)
                .appInfo(mockAppInfo)
                .serviceStatus(ServiceStatus.NORMAL)
                .notices(java.util.Arrays.asList("서비스 오픈 안내", "이벤트 진행 중"))
                .faqContent(Map.of(
                    "결제", Map.of(
                        "환불은 어떻게 하나요?", "구매 후 7일 이내에는 전액 환불 가능합니다.",
                        "결제 방법은 어떤 것이 있나요?", "신용카드, 계좌이체, 페이팔 등을 지원합니다."
                    ),
                    "일반", Map.of(
                        "앱은 어떻게 사용하나요?", "앱 사용 방법은 매뉴얼을 참고해주세요.",
                        "비밀번호를 잊어버렸어요.", "로그인 화면에서 '비밀번호 찾기'를 이용해주세요."
                    )
                ))
                .supportedLanguages(Set.of("ko", "ja", "en"))
                .build();
        mockAppInfo.setServiceStatusInfo(mockServiceStatusInfo);

        // DeploymentInfo 설정
        mockDeploymentInfo = DeploymentInfo.builder()
                .id(1L)
                .appInfo(mockAppInfo)
                .deploymentInfo("자동 배포 시스템을 통한 배포")
                .deploymentStatus("DEPLOYED")
                .lastDeploymentAt(LocalDateTime.now())
                .deploymentNotes("성능 개선 및 버그 수정")
                .build();
        mockAppInfo.setDeploymentInfo(mockDeploymentInfo);
    }

    @Test
    @DisplayName("최신 앱 정보 조회 성공")
    void getLatestAppInfo_Success() throws Exception {
        when(appInfoService.getLatestAppInfo()).thenReturn(mockAppInfo);

        mockMvc.perform(get("/api/v1/app/info"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.appName").value("Preview Service"))
                .andExpect(jsonPath("$.data.appVersion").value("1.1.0"))
                .andExpect(jsonPath("$.data.appDescription").value("AI 기반 콘텐츠 미리보기 서비스"))
                .andExpect(jsonPath("$.data.logoUrl").value("https://example.com/logo.png"))
                .andExpect(jsonPath("$.data.legalInfo.termsOfService").value("서비스 이용약관 v1.1"))
                .andExpect(jsonPath("$.data.companyInfo.companyName").value("Evawova Inc."))
                .andExpect(jsonPath("$.data.serviceStatusInfo.serviceStatus").value("NORMAL"))
                .andExpect(jsonPath("$.data.deploymentInfo.deploymentStatus").value("DEPLOYED"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("최신 앱 정보 조회 실패 - 데이터 없음")
    void getLatestAppInfo_NotFound() throws Exception {
        when(appInfoService.getLatestAppInfo()).thenReturn(null);

        mockMvc.perform(get("/api/v1/app/info"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("법적 정보 조회 성공")
    void getLegalInfo_Success() throws Exception {
        when(appInfoService.getLatestAppInfo()).thenReturn(mockAppInfo);

        mockMvc.perform(get("/api/v1/app/legal/terms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").value("서비스 이용약관 v1.1"));
    }

    @Test
    @DisplayName("법적 정보 조회 실패 - 잘못된 타입")
    void getLegalInfo_InvalidType() throws Exception {
        when(appInfoService.getLatestAppInfo()).thenReturn(mockAppInfo);

        mockMvc.perform(get("/api/v1/app/legal/invalid"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("회사 정보 조회 성공")
    void getCompanyInfo_Success() throws Exception {
        when(appInfoService.getLatestAppInfo()).thenReturn(mockAppInfo);

        mockMvc.perform(get("/api/v1/app/company"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.companyName").value("Evawova Inc."))
                .andExpect(jsonPath("$.data.contactEmail").value("support@evawova.com"))
                .andExpect(jsonPath("$.data.contactPhone").value("02-123-4567"));
    }

    @Test
    @DisplayName("FAQ 정보 조회 성공")
    void getFaq_Success() throws Exception {
        when(appInfoService.getLatestAppInfo()).thenReturn(mockAppInfo);

        mockMvc.perform(get("/api/v1/app/faq"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.결제.환불은 어떻게 하나요?").value("구매 후 7일 이내에는 전액 환불 가능합니다."))
                .andExpect(jsonPath("$.data.일반.앱은 어떻게 사용하나요?").value("앱 사용 방법은 매뉴얼을 참고해주세요."));
    }

    @Test
    @DisplayName("배포 정보 조회 성공")
    void getDeploymentInfo_Success() throws Exception {
        when(appInfoService.getLatestAppInfo()).thenReturn(mockAppInfo);

        mockMvc.perform(get("/api/v1/app/deployment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.deploymentInfo").value("자동 배포 시스템을 통한 배포"))
                .andExpect(jsonPath("$.data.deploymentStatus").value("DEPLOYED"))
                .andExpect(jsonPath("$.data.deploymentNotes").value("성능 개선 및 버그 수정"));
    }

    @Test
    @DisplayName("서비스 상태 업데이트 성공")
    void updateServiceStatus_Success() throws Exception {
        when(appInfoService.getLatestAppInfo()).thenReturn(mockAppInfo);
        
        // 유지보수 모드로 변경된 ServiceStatusInfo 설정
        ServiceStatusInfo updatedStatusInfo = ServiceStatusInfo.builder()
                .id(1L)
                .appInfo(mockAppInfo)
                .serviceStatus(ServiceStatus.MAINTENANCE)
                .serviceStatusMessage("System maintenance in progress")
                .notices(java.util.Arrays.asList("서비스 오픈 안내", "이벤트 진행 중"))
                .faqContent(mockServiceStatusInfo.getFaqContent())
                .supportedLanguages(mockServiceStatusInfo.getSupportedLanguages())
                .build();
                
        when(appInfoService.updateServiceStatus(anyLong(), any(ServiceStatus.class), anyString()))
                .thenReturn(updatedStatusInfo);

        mockMvc.perform(put("/api/v1/app/status")
                .param("status", "MAINTENANCE")
                .param("message", "System maintenance in progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.serviceStatus").value("MAINTENANCE"))
                .andExpect(jsonPath("$.data.serviceStatusMessage").value("System maintenance in progress"));
    }
    
    @Test
    @DisplayName("긴급 공지 설정 성공")
    void setEmergencyNotice_Success() throws Exception {
        when(appInfoService.getLatestAppInfo()).thenReturn(mockAppInfo);
        
        // 긴급 공지가 추가된 ServiceStatusInfo 설정
        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = startAt.plusHours(1);
        
        ServiceStatusInfo updatedStatusInfo = ServiceStatusInfo.builder()
                .id(1L)
                .appInfo(mockAppInfo)
                .serviceStatus(ServiceStatus.NORMAL)
                .emergencyNotice("Emergency maintenance")
                .emergencyNoticeStartAt(startAt)
                .emergencyNoticeEndAt(endAt)
                .emergencyNoticeActive(true)
                .notices(mockServiceStatusInfo.getNotices())
                .faqContent(mockServiceStatusInfo.getFaqContent())
                .supportedLanguages(mockServiceStatusInfo.getSupportedLanguages())
                .build();
                
        when(appInfoService.setEmergencyNotice(anyLong(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(updatedStatusInfo);

        mockMvc.perform(post("/api/v1/app/emergency-notice")
                .param("notice", "Emergency maintenance")
                .param("startAt", startAt.toString())
                .param("endAt", endAt.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.emergencyNotice").value("Emergency maintenance"))
                .andExpect(jsonPath("$.data.emergencyNoticeActive").value(true));
    }
    
    @Test
    @DisplayName("긴급 공지 해제 성공")
    void clearEmergencyNotice_Success() throws Exception {
        when(appInfoService.getLatestAppInfo()).thenReturn(mockAppInfo);
        
        // 긴급 공지가 제거된 ServiceStatusInfo 설정
        ServiceStatusInfo updatedStatusInfo = ServiceStatusInfo.builder()
                .id(1L)
                .appInfo(mockAppInfo)
                .serviceStatus(ServiceStatus.NORMAL)
                .emergencyNotice(null)
                .emergencyNoticeStartAt(null)
                .emergencyNoticeEndAt(null)
                .emergencyNoticeActive(false)
                .notices(mockServiceStatusInfo.getNotices())
                .faqContent(mockServiceStatusInfo.getFaqContent())
                .supportedLanguages(mockServiceStatusInfo.getSupportedLanguages())
                .build();
                
        when(appInfoService.clearEmergencyNotice(anyLong())).thenReturn(updatedStatusInfo);

        mockMvc.perform(delete("/api/v1/app/emergency-notice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.emergencyNoticeActive").value(false));
    }
}