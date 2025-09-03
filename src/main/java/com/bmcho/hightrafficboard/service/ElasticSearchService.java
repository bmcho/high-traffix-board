package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.entity.ArticleEntity;
import com.bmcho.hightrafficboard.exception.ArticleException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticSearchService {

    private final WebClient elasticSearchWebClient;
    private final ObjectMapper objectMapper;

    public Mono<List<Long>> articleSearch(String keyword) {
        String query = """
            {
              "_source": false,
              "query": {
                "bool": {
                  "must": [
                    { "match": { "content": "%s" }},
                    { "term":  { "isDelete": false }}
                  ]
                }
              },
              "fields": ["_id"],
              "size": 10
            }
            """.formatted(keyword);

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

    public void indexArticleDocument(ArticleEntity entity) {
        try {
            String document = objectMapper.writeValueAsString(ArticleIndex.from(entity));
            elasticSearchWebClient.put()
                .uri("/articles/_doc/{id}", entity.getId().toString())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .bodyValue(document)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(value -> {
                    log.info("[index Article result] {}", value);
                });
        } catch (JsonProcessingException e) {
            log.error("[indexArticle] Error: {}", e.getCause().getMessage());
            throw new ArticleException.ArticleIndexingException();
        }
    }

    public void incrementViewCount(String id, long viewCount) {
        String script = "{"
            + "\"script\": {"
            + "  \"source\": \"ctx._source.viewCount = params.value\","
            + "  \"params\": {\"value\": " + viewCount + "}"
            + "}"
            + "}";

        elasticSearchWebClient.post()
            .uri("/articles/_update/{id}", id)
            .header("Content-Type", "application/json")
            .bodyValue(script)
            .retrieve()
            .bodyToMono(String.class)
            .subscribe(value -> {
                log.info("[incrementViewCount result] {}", value);
            });
    }

    @Builder
    private record ArticleIndex(long id, String title, String content, long authorId, String authorName, long boardId, long viewCount, boolean isDelete, LocalDateTime createdAt, String createdBy,
                                LocalDateTime modifiedAt, String modifiedBy) {

        public static ArticleIndex from(ArticleEntity entity) {
            return ArticleIndex.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .authorId(entity.getAuthor().getId())
                .authorName(entity.getAuthor().getUsername())
                .boardId(entity.getBoard().getId())
                .viewCount(entity.getViewCount())
                .isDelete(entity.getIsDeleted())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .modifiedAt(entity.getModifiedAt())
                .modifiedBy(entity.getModifiedBy())
                .build();
        }
    }
}