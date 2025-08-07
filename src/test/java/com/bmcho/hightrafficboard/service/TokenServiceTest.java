package com.bmcho.hightrafficboard.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenServiceTest {

    @Autowired
    TokenService tokenService;

    @Test
    void tokenValidationTest() {
        String accessToken = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MSwiaWF0IjoxNzU0NTUwOTA4LCJleHAiOjE3NTQ1NTA5MDh9.JhX_UxAlhx4Y2Qd-R5PlVxkUHWmaei7LDDSIX_oMSZpT9VY_oOvzokTIWKMaxYRtrxLgBjHcqYIF_7KpBAfQ9qLzpIvOH2aQC0FRjqlPhYZLpIfHMVaAWGxOs5qH0ye4bnt_EOdAAbYwXPNt6x_dbMyKEMCT5FvT6UdeHfWuAoRnT5vr_zTF6ObhVyGt_kJXwNSh_WuJKOccer23uFj_CxpWymbeYGC6HoYM32Gau7igXJeWhuwpb-p3_NHnP0QPhrAPbdGTEYnGkWL_HROrKm23J12wgAdJFPa3KoJFx-4KjHbgWtQoQLYQq9g0VN7oEirN2O7D-TjcFJXHCb33UQ";

        Claims claims = tokenService.parseClaims(accessToken);
        Boolean validated = tokenService.validateToken(accessToken);
        System.out.println(claims);
        System.out.println(validated);
    }
}