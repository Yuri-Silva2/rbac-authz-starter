package dev.yuri.authzstarter.cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.jspecify.annotations.NonNull;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * Cache facade for user permission snapshots.
 */
public class PermissionCache {

    private final Cache<@NonNull PermissionCacheKey, @NonNull PermissionSet> cache;

    /**
     * Creates a permission cache backed by the given Caffeine cache.
     *
     * @param cache Caffeine cache used to store permission sets
     */
    public PermissionCache(Cache<@NonNull PermissionCacheKey, @NonNull PermissionSet> cache) {
        this.cache = cache;
    }

    /**
     * Gets a permission set from the cache, loading it when absent.
     *
     * @param key cache key
     * @param loader permission set supplier used on cache miss
     * @return cached or loaded permission set
     */
    public PermissionSet get(
            PermissionCacheKey key,
            Supplier<PermissionSet> loader
    ) {
        return cache.get(key, k -> loader.get());
    }

    /**
     * Gets a permission set and reports whether the value came from cache.
     *
     * @param key cache key
     * @param loader permission set supplier used on cache miss
     * @return lookup result with the permission set and cache metadata
     */
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

    /**
     * Invalidates all cached permission sets for a tenant.
     *
     * @param tenantId tenant whose cache entries should be removed
     */
    public void invalidateTenant(UUID tenantId) {
        cache.asMap().keySet()
                .removeIf(k -> k.tenantId().equals(tenantId));
    }

    /**
     * Result of a permission cache lookup.
     *
     * @param permissionSet resolved permission set
     * @param cacheHit whether the permission set was already cached
     */
    public record LookupResult(
            PermissionSet permissionSet,
            boolean cacheHit
    ) {
    }
}
