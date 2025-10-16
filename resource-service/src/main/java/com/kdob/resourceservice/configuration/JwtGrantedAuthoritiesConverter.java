package com.kdob.resourceservice.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<GrantedAuthority> result = new ArrayList<>();

        // Existing 'authorities' claim (e.g., ROLE_USER, ROLE_ADMIN)
        Collection<String> authorities = jwt.getClaimAsStringList("authorities");
        if (authorities != null) {
            for (String auth : authorities) {
                if (auth != null && !auth.isBlank()) {
                    result.add(new SimpleGrantedAuthority(auth));
                }
            }
        }

        // OAuth2 scopes -> 'SCOPE_' authorities
        Object scopeClaim = jwt.getClaims().get("scope");
        if (scopeClaim instanceof String scopeStr) {
            for (String s : scopeStr.split("\\s+")) {
                if (!s.isBlank()) {
                    result.add(new SimpleGrantedAuthority("SCOPE_" + s));
                }
            }
        } else {
            Collection<String> scopes = jwt.getClaimAsStringList("scope");
            if (scopes != null) {
                for (String s : scopes) {
                    if (s != null && !s.isBlank()) {
                        result.add(new SimpleGrantedAuthority("SCOPE_" + s));
                    }
                }
            }
        }

        Collection<String> scp = jwt.getClaimAsStringList("scp");
        if (scp != null) {
            for (String s : scp) {
                if (s != null && !s.isBlank()) {
                    result.add(new SimpleGrantedAuthority("SCOPE_" + s));
                }
            }
        }

        return result;
    }
}