package com.evawova.preview.domain.user.controller;

import com.evawova.preview.common.exception.ApiException;
import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.common.response.ResponseEntityBuilder;
import com.evawova.preview.domain.user.dto.UserDto;
import com.evawova.preview.domain.user.dto.ChangePlanRequest;
import com.evawova.preview.domain.user.dto.RegisterUserRequest;
import com.evawova.preview.domain.user.entity.PlanType;
import com.evawova.preview.domain.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntityBuilder.success(users, "사용자 목록을 성공적으로 조회했습니다.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        try {
            UserDto user = userService.getUserById(id);
            return ResponseEntityBuilder.success(user, "사용자 정보를 성공적으로 조회했습니다.");
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다: " + id);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserDto>> getUserByEmail(@PathVariable String email) {
        try {
            UserDto user = userService.getUserByEmail(email);
            return ResponseEntityBuilder.success(user, "사용자 정보를 성공적으로 조회했습니다.");
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다: " + email);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserDto>>> searchUsers(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "planType", required = false) String planType,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        // 실제 구현에서는 서비스 메서드를 호출하여 검색 결과를 반환
        // 여기서는 예시로 모든 사용자를 반환
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntityBuilder.success(users, "사용자 검색이 완료되었습니다.");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        try {
            UserDto user = userService.registerUser(
                    request.getEmail(),
                    request.getPassword(),
                    request.getName()
            );
            return ResponseEntityBuilder.created(user, "사용자가 성공적으로 등록되었습니다.");
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}/plan")
    public ResponseEntity<ApiResponse<UserDto>> changePlan(@PathVariable("id") Long id, @Valid @RequestBody ChangePlanRequest request) {
        try {
            PlanType planType = PlanType.valueOf(request.getPlanType().toUpperCase());
            UserDto user = userService.changePlan(id, planType);
            return ResponseEntityBuilder.success(user, "사용자 플랜이 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("찾을 수 없습니다")) {
                throw new ApiException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ApiException(HttpStatus.BAD_REQUEST, "유효하지 않은 플랜 타입입니다: " + request.getPlanType());
        }
    }
} 