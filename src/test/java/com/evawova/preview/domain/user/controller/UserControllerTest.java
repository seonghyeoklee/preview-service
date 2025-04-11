package com.evawova.preview.domain.user.controller;

import com.evawova.preview.domain.user.dto.UserDto;
import com.evawova.preview.domain.user.dto.UserUpdateRequest;
import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.PlanType;
import com.evawova.preview.domain.user.entity.User;
import com.evawova.preview.domain.user.service.UserService;
import com.evawova.preview.security.FirebaseTokenProvider;
import com.evawova.preview.security.TestSecurityConfig;
import com.evawova.preview.security.WithMockFirebaseUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private FirebaseTokenProvider firebaseTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private UserDto testUserDto;
    private static final String TEST_UID = "test-uid-123";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        // Plan 객체 생성
        Plan freePlan = Plan.builder()
                .id(1L)
                .type(PlanType.FREE)
                .monthlyPrice(BigDecimal.ZERO)
                .annualPrice(BigDecimal.ZERO)
                .monthlyTokenLimit(10000)
                .active(true)
                .build();

        testUser = User.builder()
                .id(1L)
                .uid(TEST_UID)
                .email("test@example.com")
                .displayName("Test User")
                .provider(User.Provider.GOOGLE)
                .role(User.Role.USER_FREE)
                .active(true)
                .photoUrl("https://example.com/photo.jpg")
                .isEmailVerified(true)
                .lastLoginAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .plan(freePlan) // Plan 설정
                .build();

        testUserDto = UserDto.fromEntity(testUser);
    }

    @Test
    @DisplayName("내 정보 조회 성공 - @WithMockFirebaseUser 사용")
    @WithMockFirebaseUser(uid = "test-uid-123", role = "USER")
    void getMyInfo_Success_WithMockFirebaseUser() throws Exception {
        given(userService.getUserByUid(TEST_UID)).willReturn(testUserDto);

        MvcResult result = mockMvc.perform(get("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("내 정보를 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data.uid").value(testUser.getUid()))
                .andExpect(jsonPath("$.data.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.data.displayName").value(testUser.getDisplayName()))
                .andExpect(jsonPath("$.data.provider").value(testUser.getProvider().name()))
                .andExpect(jsonPath("$.data.role").value(testUser.getRole().name()))
                .andExpect(jsonPath("$.data.active").value(testUser.isActive()))
                .andExpect(jsonPath("$.data.photoUrl").value(testUser.getPhotoUrl()))
                .andExpect(jsonPath("$.data.isEmailVerified").value(testUser.isEmailVerified()))
                .andReturn();

        System.out.println("Response Body: " + result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("관리자 권한으로 모든 사용자 조회")
    @WithMockFirebaseUser(uid = "admin-uid", role = "ADMIN")
    void getAllUsers_AsAdmin() throws Exception {
        given(userService.getAllUsers()).willReturn(List.of(testUserDto));

        mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("사용자 목록을 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].uid").value(testUser.getUid()));
    }

    @Test
    @DisplayName("일반 사용자가 모든 사용자 조회 시도")
    @WithMockFirebaseUser(uid = "test-uid-123", role = "USER")
    void getAllUsers_AsUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("인증되지 않은 사용자가 내 정보 조회 시도")
    void getMyInfo_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 정보 조회 시도")
    @WithMockFirebaseUser(uid = "non-existent-uid", role = "USER")
    void getMyInfo_UserNotFound() throws Exception {
        given(userService.getUserByUid("non-existent-uid"))
                .willThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(get("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockFirebaseUser(uid = "test-uid", email = "test@example.com", displayName = "Test User", role = "USER_FREE")
    @DisplayName("현재 사용자 정보를 조회할 수 있다")
    void getCurrentUser() throws Exception {
        // given
        UserDto userDto = UserDto.builder()
                .id(1L)
                .uid("test-uid")
                .email("test@example.com")
                .displayName("Test User")
                .role(User.Role.USER_FREE)
                .active(true)
                .build();

        given(userService.getUserByUid("test-uid")).willReturn(userDto);

        // when & then
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.uid").value("test-uid"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.displayName").value("Test User"))
                .andExpect(jsonPath("$.data.role").value("USER_FREE"));
    }

    @Test
    @WithMockFirebaseUser(uid = "test-uid", email = "test@example.com", displayName = "Test User", role = "USER_FREE")
    @DisplayName("현재 사용자 정보를 업데이트할 수 있다")
    void updateCurrentUser() throws Exception {
        // given
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .displayName("Updated Name")
                .build();

        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .uid("test-uid")
                .email("test@example.com")
                .displayName("Updated Name")
                .role(User.Role.USER_FREE)
                .active(true)
                .build();

        given(userService.updateUser(eq("test-uid"), any(UserUpdateRequest.class))).willReturn(updatedUserDto);

        // when & then
        mockMvc.perform(put("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.uid").value("test-uid"))
                .andExpect(jsonPath("$.data.displayName").value("Updated Name"));
    }

    @Test
    @WithMockFirebaseUser(uid = "test-uid", email = "test@example.com", displayName = "Test User", role = "USER_FREE")
    @DisplayName("이름이 너무 짧으면 업데이트에 실패한다")
    void updateCurrentUserWithInvalidName() throws Exception {
        // given
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .displayName("A") // 2자 미만
                .build();

        // when & then
        mockMvc.perform(put("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
} 