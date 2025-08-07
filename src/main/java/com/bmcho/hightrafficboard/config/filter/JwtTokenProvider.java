package com.bmcho.hightrafficboard.config.filter;

import com.bmcho.hightrafficboard.domain.UserDomain;
import com.bmcho.hightrafficboard.service.TokenService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final TokenService tokenService;

    public Authentication getAuthentication(String accessToken) {

        if (!tokenService.validateToken(accessToken)) {
            throw new JwtException("token validation filed");
        }

        UserDomain user = tokenService.getUserByAccessToken(accessToken);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails principal = new User(user.getUsername(), user.getPassword(), authorities);

        return new UsernamePasswordAuthenticationToken(principal, user.getId(), authorities);
    }

    public long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object credentials = authentication.getCredentials();
        if (credentials instanceof Long id) {
            return id;
        }
        throw new IllegalStateException("Invalid authentication credentials");
    }

}
