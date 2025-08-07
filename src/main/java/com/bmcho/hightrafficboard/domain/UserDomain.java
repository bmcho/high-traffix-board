package com.bmcho.hightrafficboard.domain;

import com.bmcho.hightrafficboard.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDomain {

    private final long id;
    private final String username;
    private final String password;
    private final String email;

    public static UserDomain fromEntity(UserEntity userEntity) {
        return UserDomain.builder()
            .id(userEntity.getId())
            .username(userEntity.getUsername())
            .password(userEntity.getPassword())
            .email(userEntity.getEmail())
            .build();
    }

}
