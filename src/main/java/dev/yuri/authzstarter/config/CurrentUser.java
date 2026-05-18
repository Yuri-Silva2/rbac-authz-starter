package dev.yuri.authzstarter.config;

/**
 * Minimal current-user model used by the authorization starter.
 */
public class CurrentUser {

    private final String externalId;
    private final String email;
    private final String name;
    private final boolean systemAdmin;

    private String tenantId;

    /**
     * Creates a current-user instance.
     *
     * @param externalId external identity provider identifier
     * @param email user email
     * @param name display name
     * @param systemAdmin whether the user should bypass permission checks
     */
    public CurrentUser(String externalId, String email, String name, boolean systemAdmin) {
        this.externalId = externalId;
        this.email = email;
        this.name = name;
        this.systemAdmin = systemAdmin;
    }

    /**
     * Returns the external identity provider identifier.
     *
     * @return external identifier
     */
    public String externalId() {
        return externalId;
    }

    /**
     * Returns the user email.
     *
     * @return user email
     */
    public String email() {
        return email;
    }

    /**
     * Returns the display name.
     *
     * @return display name
     */
    public String name() {
        return name;
    }

    /**
     * Returns whether the user bypasses permission checks.
     *
     * @return {@code true} when the user is a system administrator
     */
    public boolean isSystemAdmin() {
        return systemAdmin;
    }

    /**
     * Returns the current tenant identifier.
     *
     * @return current tenant identifier
     */
    public String tenantId() {
        return tenantId;
    }

    /**
     * Sets the current tenant identifier.
     *
     * @param tenantId current tenant identifier
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
