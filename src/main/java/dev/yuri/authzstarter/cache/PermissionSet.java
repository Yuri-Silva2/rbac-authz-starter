package dev.yuri.authzstarter.cache;

import java.util.Set;

public class PermissionSet {

    private final Set<String> permissions;

    public PermissionSet(Set<String> permissions) {
        this.permissions = Set.copyOf(permissions);
    }

    public boolean contains(String permission) {
        return permissions.contains(permission);
    }

    public int size() {
        return permissions.size();
    }
}
