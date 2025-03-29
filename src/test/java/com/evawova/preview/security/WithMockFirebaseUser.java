package com.evawova.preview.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockFirebaseUserSecurityContextFactory.class)
public @interface WithMockFirebaseUser {
    String uid() default "test-uid";
    String email() default "test@example.com";
    String displayName() default "Test User";
    String role() default "USER";
} 