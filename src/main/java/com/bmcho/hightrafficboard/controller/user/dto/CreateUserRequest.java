package com.bmcho.hightrafficboard.controller.user.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String password;
    private String username;
    private String email;
}
