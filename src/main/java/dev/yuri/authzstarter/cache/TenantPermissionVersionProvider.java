package dev.yuri.authzstarter.cache;

import java.util.UUID;

/**
 * Provides monotonically increasing permission versions per tenant.
 */
public interface TenantPermissionVersionProvider {

    /**
     * Returns the current permission version for a tenant.
     *
     * @param tenantId tenant identifier
     * @return current tenant permission version
     */
    long getTenantVersion(UUID tenantId);

    /**
     * Updates the locally visible permission version for a tenant.
     *
     * @param tenantId tenant identifier
     * @param version observed tenant permission version
     */
    void update(UUID tenantId, long version);
}
