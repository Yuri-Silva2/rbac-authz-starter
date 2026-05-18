package dev.yuri.authzstarter.decision;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dev.yuri.authzstarter.cache.PermissionCache;
import dev.yuri.authzstarter.cache.PermissionCacheKey;
import dev.yuri.authzstarter.cache.PermissionSet;
import dev.yuri.authzstarter.cache.TenantPermissionVersionProvider;
import dev.yuri.authzstarter.config.AuthzProperties;
import dev.yuri.authzstarter.config.CurrentUser;
import dev.yuri.authzstarter.snapshot.PermissionSnapshotProvider;

import java.util.UUID;

/**
 * Authorizes user actions by resolving permissions for a tenant and permission identifier.
 */
public class AuthorizationService {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationService.class);

    private final PermissionCache cache;
    private final PermissionSnapshotProvider snapshotProvider;
    private final TenantPermissionVersionProvider versionProvider;
    private final AuthzProperties.Observability observability;

    /**
     * Creates the authorization service.
     *
     * @param cache permission cache
     * @param snapshotProvider provider used to load permissions on cache misses
     * @param versionProvider tenant permission version provider
     * @param properties authorization starter properties
     */
    public AuthorizationService(
            PermissionCache cache,
            PermissionSnapshotProvider snapshotProvider,
            TenantPermissionVersionProvider versionProvider,
            AuthzProperties properties
    ) {
        this.cache = cache;
        this.snapshotProvider = snapshotProvider;
        this.versionProvider = versionProvider;
        this.observability = properties.observability();
    }

    /**
     * Checks whether a user can perform an action identified by a permission string.
     *
     * @param userId user identifier
     * @param tenantId tenant identifier
     * @param permission permission identifier to check
     * @param currentUser current authenticated user, used for system-admin bypass
     * @return {@code true} when access is allowed
     */
    public boolean can(UUID userId, UUID tenantId, String permission, CurrentUser currentUser) {
        if (currentUser != null && currentUser.isSystemAdmin()) {
            logDecision(
                    userId,
                    tenantId,
                    permission,
                    true,
                    -1L,
                    "SYSTEM_ADMIN",
                    -1
            );
            return true;
        }

        long version = versionProvider.getTenantVersion(tenantId);

        PermissionCacheKey key =
                new PermissionCacheKey(tenantId, userId, version);

        PermissionCache.LookupResult lookupResult = cache.getWithMetadata(
                key,
                () -> snapshotProvider.load(userId, tenantId)
        );

        PermissionSet permissions = lookupResult.permissionSet();
        boolean allowed = permissions.contains(permission);

        logDecision(
                userId,
                tenantId,
                permission,
                allowed,
                version,
                lookupResult.cacheHit() ? "HIT" : "MISS",
                permissions.size()
        );

        return allowed;
    }

    private void logDecision(
            UUID userId,
            UUID tenantId,
            String permission,
            boolean allowed,
            long version,
            String cacheSource,
            int permissionCount
    ) {
        if (!observability.logDecisions()) {
            return;
        }

        log.info(
                "AUTHZ_DECISION result={} permission={} userId={} tenantId={} version={} cache={} permissionCount={}",
                allowed ? "ALLOW" : "DENY",
                permission,
                userId,
                tenantId,
                version < 0 ? "n/a" : version,
                cacheSource,
                permissionCount < 0 ? "n/a" : permissionCount
        );
    }
}
