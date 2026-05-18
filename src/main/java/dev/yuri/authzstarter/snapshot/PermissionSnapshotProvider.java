package dev.yuri.authzstarter.snapshot;

import dev.yuri.authzstarter.cache.PermissionSet;

import java.util.UUID;

/**
 * Facade that loads permission snapshots through an application-provided loader.
 */
public class PermissionSnapshotProvider {

    private final PermissionSnapshotLoader loader;

    /**
     * Creates a permission snapshot provider.
     *
     * @param loader application-provided permission snapshot loader
     */
    public PermissionSnapshotProvider(PermissionSnapshotLoader loader) {
        this.loader = loader;
    }

    /**
     * Loads the permissions for a user in a tenant.
     *
     * @param userId user identifier
     * @param tenantId tenant identifier
     * @return loaded permission set
     */
    public PermissionSet load(UUID userId, UUID tenantId) {
        return loader.load(userId, tenantId);
    }
}
