package dev.yuri.authzstarter.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import dev.yuri.authzstarter.cache.PermissionCache;
import dev.yuri.authzstarter.cache.PermissionCacheKey;
import dev.yuri.authzstarter.cache.PermissionSet;

/**
 * Auto-configuration for local permission caching.
 */
@AutoConfiguration
@EnableConfigurationProperties(AuthzProperties.class)
public class CacheConfiguration {

    /**
     * Creates the cache auto-configuration.
     */
    public CacheConfiguration() {
    }

    @Bean
    @ConditionalOnMissingBean
    Cache<@NonNull PermissionCacheKey, @NonNull PermissionSet> permissionCaffeineCache(AuthzProperties properties) {
        return Caffeine.newBuilder()
                .expireAfterWrite(properties.cache().l1Ttl())
                .maximumSize(100_000)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(PermissionCache.class)
    PermissionCache permissionCache(Cache<@NonNull PermissionCacheKey, @NonNull PermissionSet> cache) {
        return new PermissionCache(cache);
    }
}
