package dev.yuri.authzstarter.snapshot;

import dev.yuri.authzstarter.cache.PermissionSet;

import java.util.UUID;

public interface PermissionSnapshotLoader {
    PermissionSet load(UUID userId, UUID tenantId);
}
