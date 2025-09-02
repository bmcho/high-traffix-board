package com.bmcho.hightrafficboard.config;

import com.bmcho.hightrafficboard.config.properties.ElasticSearchProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class ElasticSearchWebClientConfig {

    private final ElasticSearchProperties elasticSearchProperties;

    @Bean
    @Qualifier("ElasticSearchWebClient")
    public WebClient elasticSearchWebClient() {
        ElasticSearchProperties.UserInfo userInfo = elasticSearchProperties.getUserInfo();
        return WebClient.builder()
            .baseUrl(elasticSearchProperties.getHost())
            .defaultHeaders(httpHeaders -> httpHeaders.setBasicAuth(userInfo.getUsername(), userInfo.getPassword()))
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs(clientCodecConfigurer -> clientCodecConfigurer
                    .defaultCodecs()
                    .maxInMemorySize(16 * 1024 * 1025))
                .build())
            .build();
    }

}
