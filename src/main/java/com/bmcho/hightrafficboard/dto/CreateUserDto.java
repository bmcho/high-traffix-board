package com.bmcho.hightrafficboard.dto;

import lombok.Data;

@Data
public class CreateUserDto {
    private String password;
    private String username;
    private String email;
}
