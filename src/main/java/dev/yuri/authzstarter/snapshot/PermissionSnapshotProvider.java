package dev.yuri.authzstarter.snapshot;

import dev.yuri.authzstarter.cache.PermissionSet;

import java.util.UUID;

public class PermissionSnapshotProvider {

    private final PermissionSnapshotLoader loader;

    public PermissionSnapshotProvider(PermissionSnapshotLoader loader) {
        this.loader = loader;
    }

    public PermissionSet load(UUID userId, UUID tenantId) {
        return loader.load(userId, tenantId);
    }
}
