package com.evawova.preview.domain.user.controller;

import com.evawova.preview.domain.user.dto.UserDto;
import com.evawova.preview.domain.user.dto.UserLoginLogDto;
import com.evawova.preview.domain.user.entity.User;
import com.evawova.preview.domain.user.entity.UserLoginLog;
import com.evawova.preview.domain.user.repository.UserRepository;
import com.evawova.preview.domain.user.service.UserLoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 관련 API")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserLoginLogService loginLogService;

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        
        // 사용자가 존재하지 않는 경우
        if (userOpt.isEmpty()) {
            // 로그인 실패 로그 (사용자가 없으므로 null 전달)
            loginLogService.logFailedLogin(null, request, "사용자가 존재하지 않습니다.");
            return ResponseEntity.badRequest().body(Map.of("error", "사용자가 존재하지 않습니다."));
        }
        
        User user = userOpt.get();
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // 로그인 실패 로그
            loginLogService.logFailedLogin(user, request, "비밀번호가 일치하지 않습니다.");
            return ResponseEntity.badRequest().body(Map.of("error", "비밀번호가 일치하지 않습니다."));
        }
        
        // 로그인 성공 로그
        UserLoginLog loginLog = loginLogService.logSuccessfulLogin(user, request);
        
        // 실제 구현에서는 JWT 토큰 등을 발행하여 반환
        Map<String, Object> response = new HashMap<>();
        response.put("user", UserDto.fromEntity(user));
        response.put("loginInfo", Map.of(
            "loginAt", loginLog.getLoginAt(),
            "ipAddress", loginLog.getIpAddress(),
            "deviceType", loginLog.getDeviceType(),
            "browserInfo", loginLog.getBrowserInfo(),
            "osInfo", loginLog.getOsInfo()
        ));
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "로그인 내역 조회", description = "현재 사용자의 로그인 내역을 조회합니다.")
    @GetMapping("/login-history/{userId}")
    public ResponseEntity<List<UserLoginLogDto>> getLoginHistory(
            @PathVariable Long userId,
            @Parameter(description = "로그인 내역 조회 시작일 (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "로그인 내역 조회 종료일 (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "성공한 로그인만 조회 (true/false)")
            @RequestParam(required = false) Boolean onlySuccessful
    ) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        List<UserLoginLog> logs;
        
        // 날짜 범위가 지정된 경우
        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            logs = loginLogService.getLoginHistoryByDateRange(user, startDateTime, endDateTime);
        } 
        // 성공한 로그인만 조회하는 경우
        else if (onlySuccessful != null && onlySuccessful) {
            logs = loginLogService.getSuccessfulLogins(user);
        }
        // 실패한 로그인만 조회하는 경우
        else if (onlySuccessful != null && !onlySuccessful) {
            logs = loginLogService.getFailedLogins(user);
        }
        // 모든 로그인 내역 조회
        else {
            logs = loginLogService.getLoginHistory(user);
        }
        
        List<UserLoginLogDto> logDtos = logs.stream()
                .map(UserLoginLogDto::fromEntity)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(logDtos);
    }
    
    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }
} 