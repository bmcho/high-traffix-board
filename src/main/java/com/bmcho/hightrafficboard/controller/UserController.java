
package com.bmcho.hightrafficboard.controller;

import com.bmcho.hightrafficboard.dto.CreateUserDto;
import com.bmcho.hightrafficboard.dto.UserResponseDto;
import com.bmcho.hightrafficboard.entity.UserEntity;
import com.bmcho.hightrafficboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody CreateUserDto userDto) {
        UserEntity createdUser = userService.createUser(userDto);
        UserResponseDto response = UserResponseDto.from(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}