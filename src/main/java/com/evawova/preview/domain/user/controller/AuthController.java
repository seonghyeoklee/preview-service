package com.evawova.preview.domain.user.controller;

import com.evawova.preview.common.exception.ApiException;
import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.domain.user.dto.SocialLoginRequest;
import com.evawova.preview.domain.user.dto.UserDto;
import com.evawova.preview.domain.user.dto.UserLoginLogDto;
import com.evawova.preview.domain.user.entity.User;
import com.evawova.preview.domain.user.entity.UserLoginLog;
import com.evawova.preview.domain.user.repository.UserRepository;
import com.evawova.preview.domain.user.service.UserLoginLogService;
import com.evawova.preview.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final UserLoginLogService loginLogService;
    private final UserService userService;

    @PostMapping("/social-login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> socialLogin(
            @Valid @RequestBody SocialLoginRequest request,
            HttpServletRequest httpRequest) {

        // 소셜 로그인 처리
        UserDto userDto = userService.socialLogin(request);

        // 로그인 성공 로그
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        UserLoginLog loginLog = loginLogService.logSuccessfulLogin(user, httpRequest);

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("user", userDto);
        response.put("loginInfo", Map.of(
                "loginAt", loginLog.getLoginAt(),
                "ipAddress", loginLog.getIpAddress(),
                "deviceType", loginLog.getDeviceType(),
                "browserInfo", loginLog.getBrowserInfo(),
                "osInfo", loginLog.getOsInfo()));

        return ResponseEntity.ok(ApiResponse.success(response, "소셜 로그인이 완료되었습니다."));
    }

    @DeleteMapping("/withdraw/{userId}")
    public ResponseEntity<ApiResponse<Void>> withdrawUser(@PathVariable Long userId) {
        userService.withdrawUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "회원 탈퇴가 완료되었습니다."));
    }

    @GetMapping("/login-history/{userId}")
    public ResponseEntity<ApiResponse<List<UserLoginLogDto>>> getLoginHistory(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Boolean success) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        List<UserLoginLogDto> loginHistory;
        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            loginHistory = loginLogService.getLoginHistoryByDateRange(user, startDateTime, endDateTime)
                    .stream()
                    .map(UserLoginLogDto::fromEntity)
                    .toList();
        } else {
            loginHistory = loginLogService.getLoginHistory(user)
                    .stream()
                    .map(UserLoginLogDto::fromEntity)
                    .toList();
        }

        if (success != null) {
            loginHistory = loginHistory.stream()
                    .filter(log -> log.isSuccessful() == success)
                    .toList();
        }

        return ResponseEntity.ok(ApiResponse.success(loginHistory, "로그인 내역을 조회했습니다."));
    }
}