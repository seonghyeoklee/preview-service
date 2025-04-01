package com.evawova.preview.config;

import com.evawova.preview.security.FirebaseAuthenticationFilter;
import com.evawova.preview.security.FirebaseTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final FirebaseTokenProvider firebaseTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 공개 API (인증 필요 없음)
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/plans/**").permitAll()
                .requestMatchers("/api/v1/app/**").permitAll()
                
                // 관리자 전용 API
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                
                // 도메인별 권한 설정
                // 분석 관련 API - 기본 분석은 모든 사용자, 고급 분석은 STANDARD 이상, 프리미엄 분석은 PRO 이상
                .requestMatchers("/api/v1/analysis/premium/**").hasAnyRole("USER_PRO", "ADMIN")
                .requestMatchers("/api/v1/analysis/advanced/**").hasAnyRole("USER_STANDARD", "USER_PRO", "ADMIN")
                
                // 설정 관련 API - 고급 설정은 PRO 이상
                .requestMatchers("/api/v1/config/advanced/**").hasAnyRole("USER_PRO", "ADMIN")
                
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptionHandling -> 
                exceptionHandling.accessDeniedHandler(accessDeniedHandler())
            )
            .headers(headers -> headers.frameOptions().disable())
            .addFilterBefore(firebaseAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    @Bean
    public FirebaseAuthenticationFilter firebaseAuthenticationFilter() {
        return new FirebaseAuthenticationFilter(firebaseTokenProvider);
    }
} 