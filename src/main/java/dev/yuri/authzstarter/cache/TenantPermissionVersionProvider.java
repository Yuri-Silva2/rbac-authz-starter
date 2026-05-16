package dev.yuri.authzstarter.cache;

import java.util.UUID;

public interface TenantPermissionVersionProvider {

    long getTenantVersion(UUID tenantId);

    void update(UUID tenantId, long version);
}
