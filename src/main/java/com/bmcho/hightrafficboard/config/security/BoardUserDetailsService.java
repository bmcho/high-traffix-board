package com.bmcho.hightrafficboard.config.security;

import com.bmcho.hightrafficboard.domain.UserDomain;
import com.bmcho.hightrafficboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDomain user = userService.getUserByEmail(email);
        return new BoardUser(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
