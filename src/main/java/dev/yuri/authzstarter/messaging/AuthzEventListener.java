package dev.yuri.authzstarter.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import dev.yuri.authzstarter.cache.PermissionCache;
import dev.yuri.authzstarter.cache.TenantPermissionVersionProvider;
import dev.yuri.authzstarter.config.AuthzProperties;

public class AuthzEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuthzEventListener.class);

    private final TenantPermissionVersionProvider versionProvider;
    private final PermissionCache cache;
    private final AuthzProperties.Observability observability;

    public AuthzEventListener(
            TenantPermissionVersionProvider versionProvider,
            PermissionCache cache,
            AuthzProperties properties
    ) {
        this.versionProvider = versionProvider;
        this.cache = cache;
        this.observability = properties.observability();
    }

    @RabbitListener(queues = "${authz.rabbit.queue}", containerFactory = "authzRabbitListenerContainerFactory")
    public void onEvent(AuthzEvent event) {
        long localVersion = versionProvider.getTenantVersion(event.tenantId());

        if (event.version() > localVersion) {
            versionProvider.update(event.tenantId(), event.version());
            cache.invalidateTenant(event.tenantId());
            logEvent("INVALIDATED", event, localVersion);
            return;
        }

        logEvent("IGNORED", event, localVersion);
    }

    private void logEvent(String action, AuthzEvent event, long localVersion) {
        if (!observability.logEvents()) {
            return;
        }

        log.info(
                "AUTHZ_EVENT action={} type={} tenantId={} eventVersion={} localVersion={}",
                action,
                event.type(),
                event.tenantId(),
                event.version(),
                localVersion
        );
    }
}
