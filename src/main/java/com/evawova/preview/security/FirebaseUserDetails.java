package com.evawova.preview.security;

import com.evawova.preview.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class FirebaseUserDetails implements UserDetails {
    private final String uid;
    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;

    public FirebaseUserDetails(String uid) {
        this.uid = uid;
        this.user = null;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public FirebaseUserDetails(User user) {
        this.uid = user.getUid();
        this.user = user;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return uid;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user == null || user.isActive();
    }
} 