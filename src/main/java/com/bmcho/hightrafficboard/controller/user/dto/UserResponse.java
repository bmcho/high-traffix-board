package com.bmcho.hightrafficboard.controller.user.dto;

import com.bmcho.hightrafficboard.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private final long id;
    private final String username;
    private final String email;

    public static UserResponse from(UserEntity createdUser) {
        return UserResponse.builder()
            .id(createdUser.getId())
            .username(createdUser.getUsername())
            .email(createdUser.getEmail())
            .build();
    }
}
