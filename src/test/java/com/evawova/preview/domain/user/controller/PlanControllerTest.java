package com.evawova.preview.domain.user.controller;

import com.evawova.preview.domain.user.dto.PlanDto;
import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.PlanType;
import com.evawova.preview.domain.user.service.PlanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlanController.class)
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlanService planService;

    private Plan freePlan;
    private Plan standardPlan;
    private Plan proPlan;
    private List<PlanDto> planDtos;

    @BeforeEach
    void setUp() {
        // 테스트를 위한 플랜 데이터 설정
        freePlan = Plan.createPlan(
                "Free",
                PlanType.FREE,
                0,
                0,
                10000,  // 10,000 토큰
                true
        );
        standardPlan = Plan.createPlan(
                "Standard",
                PlanType.STANDARD,
                9900,
                99000,
                50000,  // 50,000 토큰
                true
        );
        proPlan = Plan.createPlan(
                "Pro",
                PlanType.PRO,
                19900,
                199000,
                100000, // 100,000 토큰
                true
        );

        planDtos = Arrays.asList(
                PlanDto.fromEntity(freePlan),
                PlanDto.fromEntity(standardPlan),
                PlanDto.fromEntity(proPlan)
        );
    }

    @Test
    @DisplayName("모든 플랜을 조회할 수 있다")
    void getAllPlans() throws Exception {
        // given
        given(planService.getAllPlans()).willReturn(planDtos);

        // when & then
        mockMvc.perform(get("/api/v1/plans")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Free"))
                .andExpect(jsonPath("$[1].name").value("Standard"))
                .andExpect(jsonPath("$[2].name").value("Pro"));
    }

    @Test
    @DisplayName("ID로 플랜을 조회할 수 있다")
    void getPlanById() throws Exception {
        // given
        given(planService.getPlanById(1L)).willReturn(PlanDto.fromEntity(freePlan));

        // when & then
        mockMvc.perform(get("/api/v1/plans/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Free"))
                .andExpect(jsonPath("$.type").value("FREE"))
                .andExpect(jsonPath("$.monthlyPrice").value(0))
                .andExpect(jsonPath("$.annualPrice").value(0))
                .andExpect(jsonPath("$.monthlyTokenLimit").value(10000))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("존재하지 않는 ID로 플랜을 조회하면 404 에러가 발생한다")
    void getPlanById_NotFound() throws Exception {
        // given
        given(planService.getPlanById(999L))
                .willThrow(new IllegalArgumentException("플랜을 찾을 수 없습니다: 999"));

        // when & then
        mockMvc.perform(get("/api/v1/plans/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("두 플랜을 비교할 수 있다")
    void comparePlans() throws Exception {
        // given
        given(planService.getPlanById(1L)).willReturn(PlanDto.fromEntity(freePlan));
        given(planService.getPlanById(2L)).willReturn(PlanDto.fromEntity(standardPlan));

        // when & then
        mockMvc.perform(get("/api/v1/plans/compare")
                        .param("plan1Id", "1")
                        .param("plan2Id", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plan1.name").value("Free"))
                .andExpect(jsonPath("$.plan2.name").value("Standard"))
                .andExpect(jsonPath("$.comparison.monthlyTokenLimit.plan1").value(10000))
                .andExpect(jsonPath("$.comparison.monthlyTokenLimit.plan2").value(50000))
                .andExpect(jsonPath("$.comparison.monthlyTokenLimit.difference").value(40000))
                .andExpect(jsonPath("$.comparison.monthlyPrice.plan1").value(0))
                .andExpect(jsonPath("$.comparison.monthlyPrice.plan2").value(9900))
                .andExpect(jsonPath("$.comparison.monthlyPrice.difference").value(9900))
                .andExpect(jsonPath("$.comparison.annualPrice.plan1").value(0))
                .andExpect(jsonPath("$.comparison.annualPrice.plan2").value(99000))
                .andExpect(jsonPath("$.comparison.annualPrice.difference").value(99000));
    }

    @Test
    @DisplayName("존재하지 않는 플랜을 비교하면 404 에러가 발생한다")
    void comparePlans_NotFound() throws Exception {
        // given
        given(planService.getPlanById(999L))
                .willThrow(new IllegalArgumentException("플랜을 찾을 수 없습니다: 999"));

        // when & then
        mockMvc.perform(get("/api/v1/plans/compare")
                        .param("plan1Id", "1")
                        .param("plan2Id", "999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
} 