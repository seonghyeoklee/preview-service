package com.evawova.preview.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.evawova.preview.security.FirebaseAuthenticationFilter;
import com.evawova.preview.security.FirebaseTokenProvider;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    // FirebaseTokenProvider를 목(mock)으로 생성
    @MockBean
    private FirebaseTokenProvider firebaseTokenProvider;
    
    // FirebaseAuthenticationFilter를 목(mock)으로 생성
    @MockBean
    private FirebaseAuthenticationFilter firebaseAuthenticationFilter;

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()
            .anyRequest().permitAll();
        
        return http.build();
    }
} 