package dev.yuri.authzstarter.cache;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RedisTenantPermissionVersionProvider implements TenantPermissionVersionProvider {

    private final StringRedisTemplate redis;
    private final Clock clock;
    private final Duration refreshInterval;
    private final String versionKeyPrefix;
    private final Map<UUID, Entry> localCache = new ConcurrentHashMap<>();

    public RedisTenantPermissionVersionProvider(
            StringRedisTemplate redis,
            Clock clock,
            Duration refreshInterval,
            String versionKeyPrefix
    ) {
        this.redis = redis;
        this.clock = clock;
        this.refreshInterval = (refreshInterval == null) ? Duration.ofSeconds(30) : refreshInterval;
        this.versionKeyPrefix = normalizePrefix(versionKeyPrefix);
    }

    @Override
    public long getTenantVersion(UUID tenantId) {
        var now = Instant.now(clock);

        Entry current = localCache.get(tenantId);
        if (current == null) {
            long loaded = loadFromRedis(tenantId);
            localCache.put(tenantId, new Entry(loaded, now));
            return loaded;
        }

        if (!refreshInterval.isZero()
                && !refreshInterval.isNegative()
                && now.isAfter(current.loadedAt().plus(refreshInterval))) {
            long loaded = loadFromRedis(tenantId);
            localCache.put(tenantId, new Entry(loaded, now));
            return loaded;
        }

        return current.version();
    }

    @Override
    public void update(UUID tenantId, long version) {
        localCache.put(tenantId, new Entry(version, Instant.now(clock)));
    }

    private long loadFromRedis(UUID tenantId) {
        String value = redis.opsForValue().get(versionKey(tenantId));
        return value == null ? 0L : Long.parseLong(value);
    }

    private String versionKey(UUID tenantId) {
        return versionKeyPrefix + ":" + tenantId + ":version";
    }

    private static String normalizePrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return "authz:tenant";
        }
        return prefix.endsWith(":") ? prefix.substring(0, prefix.length() - 1) : prefix;
    }

    private record Entry(long version, Instant loadedAt) {
    }
}
