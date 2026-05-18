package dev.yuri.authzstarter.cache;

import java.util.Objects;
import java.util.UUID;

/**
 * Cache key used to isolate permission snapshots by tenant, user and tenant version.
 *
 * @param tenantId tenant that owns the permissions
 * @param userId user whose permissions are cached
 * @param version tenant permission version used to invalidate stale entries
 */
public record PermissionCacheKey(
        UUID tenantId,
        UUID userId,
        long version
) {
    /**
     * Creates a permission cache key.
     */
    public PermissionCacheKey {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(userId, "userId");
    }
}
