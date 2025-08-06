package com.bmcho.hightrafficboard.controller.user.dto;

import lombok.Data;

@Data
public class LoginUserRequest {
    private String email;
    private String password;
}
