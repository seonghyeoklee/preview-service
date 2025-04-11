package com.evawova.preview.domain.user.controller;

import com.evawova.preview.common.exception.ApiException;
import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.domain.user.dto.SubscriptionDto;
import com.evawova.preview.domain.user.entity.Subscription;
import com.evawova.preview.domain.user.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@Tag(name = "Subscription", description = "구독 API")
@RequiredArgsConstructor
@Validated
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/users/{userId}")
    @Operation(summary = "사용자의 구독 목록 조회")
    public ResponseEntity<ApiResponse<List<SubscriptionDto>>> getUserSubscriptions(
            @Parameter(description = "사용자 ID") @PathVariable @NotNull Long userId) {
        try {
            List<SubscriptionDto> subscriptions = subscriptionService.getUserSubscriptions(userId);
            return ResponseEntity.ok(ApiResponse.success(subscriptions));
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/users/{userId}/active")
    @Operation(summary = "사용자의 활성 구독 조회")
    public ResponseEntity<ApiResponse<SubscriptionDto>> getActiveSubscription(
            @Parameter(description = "사용자 ID") @PathVariable @NotNull Long userId) {
        try {
            SubscriptionDto subscription = subscriptionService.getActiveSubscription(userId);
            return ResponseEntity.ok(ApiResponse.success(subscription));
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "새 구독 생성")
    public ResponseEntity<ApiResponse<SubscriptionDto>> createSubscription(
            @Parameter(description = "사용자 ID") @RequestParam @NotNull Long userId,
            @Parameter(description = "플랜 ID") @RequestParam @NotNull Long planId,
            @Parameter(description = "구독 주기 (MONTHLY/ANNUAL)") @RequestParam @NotNull Subscription.SubscriptionCycle cycle) {
        try {
            SubscriptionDto subscription = subscriptionService.createSubscription(userId, planId, cycle);
            return ResponseEntity.ok(ApiResponse.success(subscription, "구독이 성공적으로 생성되었습니다."));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("찾을 수 없습니다")) {
                throw new ApiException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ApiException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{subscriptionId}/cancel")
    @Operation(summary = "구독 취소")
    public ResponseEntity<ApiResponse<SubscriptionDto>> cancelSubscription(
            @Parameter(description = "구독 ID") @PathVariable @NotNull Long subscriptionId) {
        try {
            SubscriptionDto subscription = subscriptionService.cancelSubscription(subscriptionId);
            return ResponseEntity.ok(ApiResponse.success(subscription, "구독이 성공적으로 취소되었습니다."));
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{subscriptionId}/renew")
    @Operation(summary = "구독 갱신")
    public ResponseEntity<ApiResponse<SubscriptionDto>> renewSubscription(
            @Parameter(description = "구독 ID") @PathVariable @NotNull Long subscriptionId) {
        try {
            SubscriptionDto subscription = subscriptionService.renewSubscription(subscriptionId);
            return ResponseEntity.ok(ApiResponse.success(subscription, "구독이 성공적으로 갱신되었습니다."));
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}