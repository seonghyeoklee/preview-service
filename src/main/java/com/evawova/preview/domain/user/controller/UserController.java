package com.evawova.preview.domain.user.controller;

import com.evawova.preview.domain.user.dto.UserDto;
import com.evawova.preview.domain.user.entity.PlanType;
import com.evawova.preview.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "모든 사용자 조회", description = "시스템에 등록된 모든 사용자를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "ID로 사용자 조회", description = "사용자 ID로 사용자 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        try {
            UserDto user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "이메일로 사용자 조회", description = "이메일 주소로 사용자 정보를 조회합니다.")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        try {
            UserDto user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "사용자 검색", 
        description = "이름, 플랜 타입 등으로 사용자를 검색합니다. 모든 파라미터는 선택적입니다."
    )
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(
        @Parameter(description = "검색할 사용자 이름 (부분 일치)") @RequestParam(required = false) String name,
        @Parameter(description = "검색할 플랜 타입 (FREE, STANDARD, PRO)") @RequestParam(required = false) String planType,
        @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size
    ) {
        // 실제 구현에서는 서비스 메서드를 호출하여 검색 결과를 반환
        // 여기서는 예시로 모든 사용자를 반환
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "사용자 등록", description = "새로운 사용자를 등록합니다. 기본 Free 플랜으로 설정됩니다.")
    @PostMapping
    public ResponseEntity<UserDto> registerUser(@RequestBody RegisterUserRequest request) {
        try {
            UserDto user = userService.registerUser(
                    request.getEmail(),
                    request.getPassword(),
                    request.getName()
            );
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "사용자 플랜 변경", description = "사용자의 플랜을 변경합니다.")
    @PutMapping("/{id}/plan")
    public ResponseEntity<UserDto> changePlan(@PathVariable Long id, @RequestBody ChangePlanRequest request) {
        try {
            PlanType planType = PlanType.valueOf(request.getPlanType().toUpperCase());
            UserDto user = userService.changePlan(id, planType);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Request DTO 클래스들
    @Data
    public static class RegisterUserRequest {
        private String email;
        private String password;
        private String name;
    }

    @Data
    public static class ChangePlanRequest {
        private String planType;
    }
} 