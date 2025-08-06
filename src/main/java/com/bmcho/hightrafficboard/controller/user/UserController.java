
package com.bmcho.hightrafficboard.controller.user;

import com.bmcho.hightrafficboard.config.filter.JwtTokenProvider;
import com.bmcho.hightrafficboard.config.security.BoardUser;
import com.bmcho.hightrafficboard.controller.user.dto.CreateUserRequest;
import com.bmcho.hightrafficboard.controller.user.dto.LoginUserRequest;
import com.bmcho.hightrafficboard.controller.user.dto.UserResponse;
import com.bmcho.hightrafficboard.entity.UserEntity;
import com.bmcho.hightrafficboard.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("sign-in")
    public ResponseEntity<String> loginUser(@RequestBody LoginUserRequest loginUserRequest) {
        String email = loginUserRequest.getEmail();
        String password = loginUserRequest.getPassword();

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);

        Authentication authentication = authenticationManager.authenticate(token);
        BoardUser user = (BoardUser) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.generatedToken(user.getId());

        return ResponseEntity.status(HttpStatus.OK).body(accessToken);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest userDto) {
        UserEntity createdUser = userService.createUser(userDto);
        UserResponse response = UserResponse.from(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> createUser(
        @Parameter(description = "ID of the user to be deleted", required = true)
        @PathVariable long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}