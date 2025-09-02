package com.bmcho.hightrafficboard.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "elastic-search")
public class ElasticSearchProperties {

    private String host;
    private UserInfo userInfo;


    @Getter
    @Setter
    public static class UserInfo {
        private String username;
        private String password;
    }

}
