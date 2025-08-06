package com.bmcho.hightrafficboard.config.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class BoardUser extends User {
    private final long id;
    private final String username;
    private final String email;

    public BoardUser(
        long id,
        String username,
        String email,
        Collection<? extends GrantedAuthority> authorities
    ) {
        super(email, null, authorities);
        this.id = id;
        this.username = username;
        this.email = email;
    }

}
