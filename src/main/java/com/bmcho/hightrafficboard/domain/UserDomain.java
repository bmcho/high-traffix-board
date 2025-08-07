package com.bmcho.hightrafficboard.domain;

import com.bmcho.hightrafficboard.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserDomain {

    private final long id;
    private String username;
    private String password;
    private String email;

    public static UserDomain fromEntity(UserEntity userEntity) {
        return UserDomain.builder()
            .id(userEntity.getId())
            .username(userEntity.getUsername())
            .password(userEntity.getPassword())
            .email(userEntity.getEmail())
            .build();
    }

}
