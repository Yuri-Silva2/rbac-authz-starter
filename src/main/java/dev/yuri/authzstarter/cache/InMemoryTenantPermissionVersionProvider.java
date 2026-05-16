package dev.yuri.authzstarter.cache;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTenantPermissionVersionProvider implements TenantPermissionVersionProvider {

    private final Map<UUID, Long> versions = new ConcurrentHashMap<>();

    @Override
    public long getTenantVersion(UUID tenantId) {
        return versions.getOrDefault(tenantId, 0L);
    }

    @Override
    public void update(UUID tenantId, long version) {
        versions.merge(tenantId, version, Math::max);
    }
}
