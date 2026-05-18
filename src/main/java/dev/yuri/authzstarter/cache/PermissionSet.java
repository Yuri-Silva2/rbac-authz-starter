package dev.yuri.authzstarter.cache;

import java.util.Set;

/**
 * Immutable set of permission identifiers assigned to a user in a tenant.
 */
public class PermissionSet {

    private final Set<String> permissions;

    /**
     * Creates a permission set from the given permission identifiers.
     *
     * @param permissions permission identifiers to expose through this set
     */
    public PermissionSet(Set<String> permissions) {
        this.permissions = Set.copyOf(permissions);
    }

    /**
     * Checks whether the permission identifier is present.
     *
     * @param permission permission identifier to check
     * @return {@code true} when the permission is present
     */
    public boolean contains(String permission) {
        return permissions.contains(permission);
    }

    /**
     * Returns the number of permissions in this set.
     *
     * @return number of permissions
     */
    public int size() {
        return permissions.size();
    }
}
