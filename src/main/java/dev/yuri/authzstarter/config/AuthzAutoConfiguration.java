package dev.yuri.authzstarter.config;

import dev.yuri.authzstarter.cache.InMemoryTenantPermissionVersionProvider;
import dev.yuri.authzstarter.cache.PermissionCache;
import dev.yuri.authzstarter.cache.TenantPermissionVersionProvider;
import dev.yuri.authzstarter.decision.AuthorizationService;
import dev.yuri.authzstarter.snapshot.PermissionSnapshotLoader;
import dev.yuri.authzstarter.snapshot.PermissionSnapshotProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@AutoConfiguration(after = CacheConfiguration.class)
@EnableConfigurationProperties(AuthzProperties.class)
public class AuthzAutoConfiguration {

    @Bean
    @ConditionalOnBean(PermissionSnapshotLoader.class)
    @ConditionalOnMissingBean
    public PermissionSnapshotProvider permissionSnapshotProvider(PermissionSnapshotLoader loader) {
        return new PermissionSnapshotProvider(loader);
    }

    @Bean
    @ConditionalOnMissingBean
    public Clock authzClock() {
        return Clock.systemUTC();
    }

    @Bean
    @ConditionalOnMissingBean
    public TenantPermissionVersionProvider tenantPermissionVersionProvider() {
        return new InMemoryTenantPermissionVersionProvider();
    }

    @Bean(name = "authz")
    @ConditionalOnBean(PermissionSnapshotProvider.class)
    @ConditionalOnMissingBean
    public AuthorizationService authorizationService(
            PermissionCache cache,
            PermissionSnapshotProvider snapshotProvider,
            TenantPermissionVersionProvider versionProvider,
            AuthzProperties properties
    ) {
        return new AuthorizationService(cache, snapshotProvider, versionProvider, properties);
    }
}
