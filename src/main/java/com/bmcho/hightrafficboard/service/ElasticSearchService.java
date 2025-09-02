package com.bmcho.hightrafficboard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticSearchService {

    private final WebClient elasticSearchWebClient;
    private final ObjectMapper objectMapper;

    public Mono<List<Long>> articleSearch(String keyword) {
        String query = String.format(
            "{\n" +
                "  \"_source\": false,\n" +
                "  \"query\": {\n" +
                "    \"bool\": {\n" +
                "      \"must\": [\n" +
                "        { \"match\": { \"content\": \"%s\" }},\n" +
                "        { \"term\":  { \"isDeleted\": false }}\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"fields\": [\"_id\"],\n" +
                "  \"size\": 10\n" +
                "}",
            keyword
        );
        return elasticSearchWebClient.post()
                .uri("/articles/_search")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .bodyValue(query)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::extractIds);
    }

    private Mono<List<Long>> extractIds(String responseBody) {
        List<Long> ids = new ArrayList<>();
        try {
            JsonNode hits = objectMapper.readTree(responseBody).path("hits").path("hits");
            hits.forEach(hit -> ids.add(hit.path("_id").asLong()));
        } catch (IOException e) {
            return Mono.error(e);
        }
        return Mono.just(ids);
    }

    public Mono<String> indexArticleDocument(String id, String document) {
        return elasticSearchWebClient.put()
                .uri("/articles/_doc/{id}", id)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .bodyValue(document)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> incrementViewCount(String id, long viewCount) {
        String script = "{"
            + "\"script\": {"
            + "  \"source\": \"ctx._source.viewCount += params.increment\","
            + "  \"params\": {\"increment\": " + viewCount + "}"
            + "}"
            + "}";

        return elasticSearchWebClient.post()
            .uri("/articles/_update/{id}", id)
            .header("Content-Type", "application/json")
            .bodyValue(script)
            .retrieve()
            .bodyToMono(String.class);
    }
}