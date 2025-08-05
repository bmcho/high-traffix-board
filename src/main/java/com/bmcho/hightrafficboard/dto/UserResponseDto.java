package com.bmcho.hightrafficboard.dto;

import com.bmcho.hightrafficboard.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
    private long id;
    private String username;
    private String email;

    public static UserResponseDto from(UserEntity createdUser) {
        return UserResponseDto.builder()
            .id(createdUser.getId())
            .username(createdUser.getUsername())
            .email(createdUser.getEmail())
            .build();
    }
}
