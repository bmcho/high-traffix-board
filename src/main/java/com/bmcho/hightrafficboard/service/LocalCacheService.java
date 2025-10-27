package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.entity.ArticleEntity;
import com.bmcho.hightrafficboard.exception.BasicException;
import com.bmcho.hightrafficboard.repository.ArticleRepository;
import jakarta.persistence.Basic;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ConcurrentLruCache;

@Service
public class LocalCacheService {

    private final ConcurrentLruCache<Long, ArticleEntity> cache;
    private final ArticleRepository articleRepository;

    public LocalCacheService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
        this.cache = new ConcurrentLruCache<>(100, key -> this.articleRepository
                .findById(key)
                .orElseThrow(() ->
                        new BasicException(key + " article not found in local cache")));
    }

    public ArticleEntity getArticle(long articleId) {
        try {
            return this.cache.get(articleId);
        } catch (BasicException exception) {
            return null;
        }
    }

    public ArticleEntity updateArticle(long articleId) {
        this.cache.remove(articleId);
        return this.cache.get(articleId);
    }

}
