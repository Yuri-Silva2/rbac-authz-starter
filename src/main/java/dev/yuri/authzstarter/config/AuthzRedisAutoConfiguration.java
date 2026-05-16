package dev.yuri.authzstarter.config;

import dev.yuri.authzstarter.cache.RedisTenantPermissionVersionProvider;
import dev.yuri.authzstarter.cache.TenantPermissionVersionProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Clock;

@AutoConfiguration(before = AuthzAutoConfiguration.class)
@EnableConfigurationProperties(AuthzProperties.class)
@ConditionalOnClass(StringRedisTemplate.class)
@ConditionalOnBean(StringRedisTemplate.class)
@ConditionalOnProperty(prefix = "authz.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AuthzRedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TenantPermissionVersionProvider tenantPermissionVersionProvider(
            StringRedisTemplate redis,
            Clock authzClock,
            AuthzProperties properties
    ) {
        return new RedisTenantPermissionVersionProvider(
                redis,
                authzClock,
                properties.cache().versionRefreshInterval(),
                properties.redis().versionKeyPrefix()
        );
    }
}
