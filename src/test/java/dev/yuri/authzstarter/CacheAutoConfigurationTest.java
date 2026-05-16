package dev.yuri.authzstarter;

import dev.yuri.authzstarter.cache.PermissionCache;
import dev.yuri.authzstarter.cache.TenantPermissionVersionProvider;
import dev.yuri.authzstarter.config.AuthzAutoConfiguration;
import dev.yuri.authzstarter.config.CacheConfiguration;
import dev.yuri.authzstarter.decision.AuthorizationService;
import dev.yuri.authzstarter.snapshot.PermissionSnapshotLoader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CacheAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(
                            CacheConfiguration.class,
                            AuthzAutoConfiguration.class
                    ));

    @Test
    void createsPermissionCacheAndInMemoryVersionProvider() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(PermissionCache.class);
            assertThat(context).hasSingleBean(TenantPermissionVersionProvider.class);
        });
    }

    @Test
    void createsAuthorizationServiceWhenSnapshotLoaderExists() {
        contextRunner
                .withBean(PermissionSnapshotLoader.class, () -> (userId, tenantId) -> new dev.yuri.authzstarter.cache.PermissionSet(Set.of("workspace.read")))
                .run(context -> assertThat(context).hasSingleBean(AuthorizationService.class));
    }
}
