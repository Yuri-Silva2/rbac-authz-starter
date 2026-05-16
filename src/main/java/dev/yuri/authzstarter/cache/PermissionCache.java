package dev.yuri.authzstarter.cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.jspecify.annotations.NonNull;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class PermissionCache {

    private final Cache<@NonNull PermissionCacheKey, @NonNull PermissionSet> cache;

    public PermissionCache(Cache<@NonNull PermissionCacheKey, @NonNull PermissionSet> cache) {
        this.cache = cache;
    }

    public PermissionSet get(
            PermissionCacheKey key,
            Supplier<PermissionSet> loader
    ) {
        return cache.get(key, k -> loader.get());
    }

    public LookupResult getWithMetadata(
            PermissionCacheKey key,
            Supplier<PermissionSet> loader
    ) {
        AtomicBoolean loaded = new AtomicBoolean(false);
        PermissionSet permissionSet = cache.get(key, k -> {
            loaded.set(true);
            return loader.get();
        });
        return new LookupResult(permissionSet, !loaded.get());
    }

    public void invalidateTenant(UUID tenantId) {
        cache.asMap().keySet()
                .removeIf(k -> k.tenantId().equals(tenantId));
    }

    public record LookupResult(
            PermissionSet permissionSet,
            boolean cacheHit
    ) {
    }
}
