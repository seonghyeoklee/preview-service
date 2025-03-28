package com.evawova.preview.domain.user.service;

import com.evawova.preview.domain.user.dto.UserDto;
import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.PlanType;
import com.evawova.preview.domain.user.entity.User;
import com.evawova.preview.domain.user.repository.PlanRepository;
import com.evawova.preview.domain.user.repository.UserRepository;
import com.evawova.preview.infrastructure.events.DomainEventDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + id));
        return UserDto.fromEntity(user);
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserDto registerUser(String email, String password, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다: " + email);
        }

        // 기본 FREE 플랜으로 등록
        Plan freePlan = planRepository.findByType(PlanType.FREE)
                .orElseThrow(() -> new IllegalArgumentException("기본 플랜을 찾을 수 없습니다"));

        User user = User.createUser(
                email,
                passwordEncoder.encode(password),
                name,
                freePlan
        );

        User savedUser = userRepository.save(user);
        
        // 도메인 이벤트 발행
        eventPublisher.publishEvent(savedUser);
        
        return UserDto.fromEntity(savedUser);
    }

    @Transactional
    public UserDto changePlan(Long userId, PlanType planType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        Plan newPlan = planRepository.findByType(planType)
                .orElseThrow(() -> new IllegalArgumentException("플랜을 찾을 수 없습니다: " + planType));

        user.changePlan(newPlan);
        
        // 도메인 이벤트 발행
        eventPublisher.publishEvent(user);
        
        return UserDto.fromEntity(user);
    }
} 