package dev.yuri.authzstarter.config;

public class CurrentUser {

    private final String externalId;
    private final String email;
    private final String name;
    private final boolean systemAdmin;

    private String tenantId;

    public CurrentUser(String externalId, String email, String name, boolean systemAdmin) {
        this.externalId = externalId;
        this.email = email;
        this.name = name;
        this.systemAdmin = systemAdmin;
    }

    public String externalId() {
        return externalId;
    }

    public String email() {
        return email;
    }

    public String name() {
        return name;
    }

    public boolean isSystemAdmin() {
        return systemAdmin;
    }

    public String tenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
