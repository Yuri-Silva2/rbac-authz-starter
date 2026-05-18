package dev.yuri.authzstarter.snapshot;

import dev.yuri.authzstarter.cache.PermissionSet;

import java.util.UUID;

/**
 * Application extension point for loading permissions from the source of truth.
 */
public interface PermissionSnapshotLoader {
    /**
     * Loads all permissions assigned to a user in a tenant.
     *
     * @param userId user identifier
     * @param tenantId tenant identifier
     * @return loaded permission set
     */
    PermissionSet load(UUID userId, UUID tenantId);
}
