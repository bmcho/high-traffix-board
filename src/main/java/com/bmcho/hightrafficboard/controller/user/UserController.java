
package com.bmcho.hightrafficboard.controller.user;

import com.bmcho.hightrafficboard.config.security.BoardUser;
import com.bmcho.hightrafficboard.controller.BoardApiResponse;
import com.bmcho.hightrafficboard.controller.user.dto.CreateUserRequest;
import com.bmcho.hightrafficboard.controller.user.dto.LoginUserRequest;
import com.bmcho.hightrafficboard.controller.user.dto.UserResponse;
import com.bmcho.hightrafficboard.entity.UserEntity;
import com.bmcho.hightrafficboard.entity.mongo.UserNotificationHistory;
import com.bmcho.hightrafficboard.service.JwtBlacklistService;
import com.bmcho.hightrafficboard.service.TokenService;
import com.bmcho.hightrafficboard.service.UserNotificationHistoryService;
import com.bmcho.hightrafficboard.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenService tokenService;
    private final JwtBlacklistService jwtBlacklistService;

    private final UserNotificationHistoryService userNotificationHistoryService;

    @PostMapping("sign-in")
    public BoardApiResponse<String> loginUser(@RequestBody LoginUserRequest loginUserRequest, HttpServletResponse response) {
        String email = loginUserRequest.getEmail();
        String password = loginUserRequest.getPassword();

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);

        Authentication authentication = authenticationManager.authenticate(token);
        BoardUser user = (BoardUser) authentication.getPrincipal();
        String accessToken = tokenService.generatedAccessToken(user);

        Cookie cookie = new Cookie("board_token", accessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);

        return BoardApiResponse.ok(accessToken);
    }

    @PostMapping("/sign-up")
    public BoardApiResponse<UserResponse> createUser(@RequestBody CreateUserRequest userDto) {
        UserEntity createdUser = userService.createUser(userDto);
        UserResponse response = UserResponse.from(createdUser);
        return BoardApiResponse.ok(response);
    }

    @DeleteMapping("/{userId}")
    public BoardApiResponse<Void> deleteUser(
        @Parameter(description = "ID of the user to be deleted", required = true)
        @PathVariable long userId) {
        userService.deleteUser(userId);
        return BoardApiResponse.ok(null);
    }

    @PostMapping("/logout")
    public BoardApiResponse<Void> logout(@CookieValue(value = "board_token", required = false) String cookieToken,
                                         HttpServletRequest request, HttpServletResponse response) {

        String token;
        if (cookieToken != null) {
            token = cookieToken;
        } else {
            String bearerToken = request.getHeader("Authorization");
            token = bearerToken.substring(7);
        }

        jwtBlacklistService.blacklistToken(token);
        Cookie cookie = new Cookie("board_token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 삭제
        response.addCookie(cookie);


        return BoardApiResponse.ok(null);
    }

    @PostMapping("/history")
    public BoardApiResponse<String> readHistory(@RequestParam String historyId) {
        userNotificationHistoryService.readNotification(historyId);
    }

    @GetMapping("/history")
    public BoardApiResponse<List<UserNotificationHistory>> getHistoryList() {
        return BoardApiResponse.ok(userNotificationHistoryService.getNotificationList());
    }
}