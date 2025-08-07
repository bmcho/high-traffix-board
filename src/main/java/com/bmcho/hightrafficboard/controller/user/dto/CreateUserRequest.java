package com.bmcho.hightrafficboard.controller.user.dto;

import com.bmcho.hightrafficboard.anntation.PasswordEncryption;
import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    @PasswordEncryption
    private String password;
    private String email;
}
