package dev.yuri.authzstarter.cache;

import java.util.Objects;
import java.util.UUID;

public record PermissionCacheKey(
        UUID tenantId,
        UUID userId,
        long version
) {
    public PermissionCacheKey {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(userId, "userId");
    }
}
