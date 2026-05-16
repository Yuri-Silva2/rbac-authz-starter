package dev.yuri.authzstarter.messaging;

import java.time.Instant;
import java.util.UUID;

public record AuthzEvent(
        UUID eventId,
        AuthzEventType type,
        UUID tenantId,
        long version,
        Instant occurredAt
) {
}
