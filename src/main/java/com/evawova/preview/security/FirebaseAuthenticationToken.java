package com.evawova.preview.security;

import com.evawova.preview.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {
    private final String uid;
    private final FirebaseUserDetails principal;

    public FirebaseAuthenticationToken(String uid) {
        super(null);
        this.uid = uid;
        this.principal = new FirebaseUserDetails(uid);
        setAuthenticated(true);
    }

    public FirebaseAuthenticationToken(FirebaseUserDetails principal) {
        super(principal.getAuthorities());
        this.uid = principal.getUid();
        this.principal = principal;
        setAuthenticated(true);
    }

    public FirebaseAuthenticationToken(User user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.uid = user.getUid();
        this.principal = new FirebaseUserDetails(user);
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
} 