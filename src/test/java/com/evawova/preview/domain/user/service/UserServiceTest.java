package com.evawova.preview.domain.user.service;

import com.evawova.preview.domain.user.dto.UserDto;
import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.PlanType;
import com.evawova.preview.domain.user.entity.User;
import com.evawova.preview.domain.user.repository.PlanRepository;
import com.evawova.preview.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    private Plan freePlan;
    private Plan standardPlan;
    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트를 위한 플랜 데이터 설정
        freePlan = Plan.createPlan(
                "Free",
                PlanType.FREE,
                0,
                0,
                1,
                3,
                false,
                false,
                false
        );
        standardPlan = Plan.createPlan(
                "Standard",
                PlanType.STANDARD,
                9900,
                99000,
                10,
                10,
                true,
                false,
                false
        );

        // 테스트를 위한 사용자 설정
        testUser = User.createUser(
                "test@example.com",
                "encoded_password",
                "Test User",
                freePlan
        );
    }

    @Test
    @DisplayName("모든 사용자 조회")
    void getAllUsers() {
        // given
        User user1 = User.createUser("user1@example.com", "password1", "User 1", freePlan);
        User user2 = User.createUser("user2@example.com", "password2", "User 2", standardPlan);
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // when
        List<UserDto> users = userService.getAllUsers();

        // then
        assertThat(users).hasSize(2);
        assertThat(users.get(0).getEmail()).isEqualTo("user1@example.com");
        assertThat(users.get(1).getEmail()).isEqualTo("user2@example.com");
    }

    @Test
    @DisplayName("ID로 사용자 조회 - 성공")
    void getUserById_Success() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // when
        UserDto userDto = userService.getUserById(1L);

        // then
        assertThat(userDto.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(userDto.getName()).isEqualTo(testUser.getName());
    }

    @Test
    @DisplayName("ID로 사용자 조회 - 실패 (존재하지 않는 ID)")
    void getUserById_Fail_UserNotFound() {
        // given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("이메일로 사용자 조회 - 성공")
    void getUserByEmail_Success() {
        // given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // when
        UserDto userDto = userService.getUserByEmail("test@example.com");

        // then
        assertThat(userDto.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(userDto.getName()).isEqualTo(testUser.getName());
    }

    @Test
    @DisplayName("이메일로 사용자 조회 - 실패 (존재하지 않는 이메일)")
    void getUserByEmail_Fail_UserNotFound() {
        // given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserByEmail("nonexistent@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("사용자 등록 - 성공")
    void registerUser_Success() {
        // given
        String email = "new@example.com";
        String password = "password";
        String name = "New User";
        String encodedPassword = "encoded_password";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(planRepository.findByType(PlanType.FREE)).thenReturn(Optional.of(freePlan));
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> {
            User savedUser = userCaptor.getValue();
            savedUser.getId(); // simulate ID setting (usually done by JPA)
            return savedUser;
        });

        // when
        UserDto userDto = userService.registerUser(email, password, name);

        // then
        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getEmail()).isEqualTo(email);
        assertThat(capturedUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(capturedUser.getName()).isEqualTo(name);
        assertThat(capturedUser.getPlan()).isEqualTo(freePlan);
        
        verify(eventPublisher).publishEvent(any(User.class));
    }

    @Test
    @DisplayName("사용자 등록 - 실패 (이미 존재하는 이메일)")
    void registerUser_Fail_EmailAlreadyExists() {
        // given
        String email = "existing@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.registerUser(email, "password", "Name"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 등록된 이메일입니다");
    }

    @Test
    @DisplayName("플랜 변경 - 성공")
    void changePlan_Success() {
        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(planRepository.findByType(PlanType.STANDARD)).thenReturn(Optional.of(standardPlan));

        // when
        UserDto userDto = userService.changePlan(userId, PlanType.STANDARD);

        // then
        assertThat(testUser.getPlan()).isEqualTo(standardPlan);
        verify(eventPublisher).publishEvent(testUser);
    }

    @Test
    @DisplayName("플랜 변경 - 실패 (존재하지 않는 사용자)")
    void changePlan_Fail_UserNotFound() {
        // given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.changePlan(userId, PlanType.STANDARD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("플랜 변경 - 실패 (존재하지 않는 플랜)")
    void changePlan_Fail_PlanNotFound() {
        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(planRepository.findByType(PlanType.STANDARD)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.changePlan(userId, PlanType.STANDARD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("플랜을 찾을 수 없습니다");
    }
} 