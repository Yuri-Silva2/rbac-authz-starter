package dev.yuri.authzstarter.messaging;

/**
 * Type of authorization change represented by an {@link AuthzEvent}.
 */
public enum AuthzEventType {
    /**
     * Permissions assigned to a role changed.
     */
    ROLE_PERMISSION_CHANGED,

    /**
     * Roles assigned to a user changed.
     */
    USER_ROLE_CHANGED,

    /**
     * A role was deleted.
     */
    ROLE_DELETED
}
