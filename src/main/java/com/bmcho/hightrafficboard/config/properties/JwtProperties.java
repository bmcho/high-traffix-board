package com.bmcho.hightrafficboard.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String privateKey;
    private String publicKey;

    private Expire expire = new Expire();

    @Getter
    @Setter
    public static class Expire {
        private int accessToken;
        private int refreshToken;
    }
}