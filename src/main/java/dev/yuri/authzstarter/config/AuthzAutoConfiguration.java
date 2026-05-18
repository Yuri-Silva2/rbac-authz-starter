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

/**
 * Core auto-configuration for the RBAC authorization starter.
 */
@AutoConfiguration(after = CacheConfiguration.class)
@EnableConfigurationProperties(AuthzProperties.class)
public class AuthzAutoConfiguration {

    /**
     * Creates the core auto-configuration.
     */
    public AuthzAutoConfiguration() {
    }

    /**
     * Creates the default permission snapshot provider when a loader is available.
     *
     * @param loader application-provided permission snapshot loader
     * @return permission snapshot provider
     */
    @Bean
    @ConditionalOnBean(PermissionSnapshotLoader.class)
    @ConditionalOnMissingBean
    public PermissionSnapshotProvider permissionSnapshotProvider(PermissionSnapshotLoader loader) {
        return new PermissionSnapshotProvider(loader);
    }

    /**
     * Provides the default UTC clock used by the starter.
     *
     * @return UTC clock
     */
    @Bean
    @ConditionalOnMissingBean
    public Clock authzClock() {
        return Clock.systemUTC();
    }

    /**
     * Creates the fallback in-memory tenant permission version provider.
     *
     * @return tenant permission version provider
     */
    @Bean
    @ConditionalOnMissingBean
    public TenantPermissionVersionProvider tenantPermissionVersionProvider() {
        return new InMemoryTenantPermissionVersionProvider();
    }

    /**
     * Creates the default authorization service bean.
     *
     * @param cache permission cache
     * @param snapshotProvider permission snapshot provider
     * @param versionProvider tenant permission version provider
     * @param properties authorization starter properties
     * @return authorization service
     */
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
