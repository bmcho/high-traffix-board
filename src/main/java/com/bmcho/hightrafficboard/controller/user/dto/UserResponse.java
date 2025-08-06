package com.bmcho.hightrafficboard.controller.user.dto;

import com.bmcho.hightrafficboard.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private long id;
    private String username;
    private String email;

    public static UserResponse from(UserEntity createdUser) {
        return UserResponse.builder()
            .id(createdUser.getId())
            .username(createdUser.getUsername())
            .email(createdUser.getEmail())
            .build();
    }
}
