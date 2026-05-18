package dev.yuri.authzstarter.messaging;

import java.time.Instant;
import java.util.UUID;

/**
 * Event that signals a tenant authorization state change.
 *
 * @param eventId unique event identifier
 * @param type authorization event type
 * @param tenantId tenant affected by the change
 * @param version tenant permission version after the change
 * @param occurredAt instant when the event occurred
 */
public record AuthzEvent(
        UUID eventId,
        AuthzEventType type,
        UUID tenantId,
        long version,
        Instant occurredAt
) {
}
