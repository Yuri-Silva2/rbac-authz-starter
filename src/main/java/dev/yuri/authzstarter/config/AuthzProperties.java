package dev.yuri.authzstarter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration properties for the RBAC authorization starter.
 *
 * @param cache cache-related settings
 * @param redis Redis integration settings
 * @param rabbit RabbitMQ integration settings
 * @param observability logging and observability settings
 */
@ConfigurationProperties(prefix = "authz")
public record AuthzProperties(
        Cache cache,
        Redis redis,
        Rabbit rabbit,
        Observability observability
) {

    /**
     * Creates authorization properties, applying defaults for missing nested groups.
     */
    public AuthzProperties {
        cache = (cache == null) ? new Cache(null, null) : cache;
        redis = (redis == null) ? new Redis(true, null) : redis;
        rabbit = (rabbit == null) ? new Rabbit(false, null, null, null) : rabbit;
        observability = (observability == null) ? new Observability(false, false) : observability;
    }

    /**
     * Cache configuration properties.
     *
     * @param l1Ttl time-to-live for local permission cache entries
     * @param versionRefreshInterval interval for refreshing Redis-backed tenant versions
     */
    public record Cache(
            Duration l1Ttl,
            Duration versionRefreshInterval
    ) {
        /**
         * Creates cache properties, applying default durations when absent.
         */
        public Cache {
            l1Ttl = (l1Ttl == null) ? Duration.ofMinutes(10) : l1Ttl;
            versionRefreshInterval = (versionRefreshInterval == null) ? Duration.ofSeconds(30) : versionRefreshInterval;
        }
    }

    /**
     * Redis integration configuration properties.
     *
     * @param enabled whether Redis-backed tenant version tracking is enabled
     * @param versionKeyPrefix prefix used to compose tenant version keys
     */
    public record Redis(
            boolean enabled,
            String versionKeyPrefix
    ) {
        /**
         * Creates Redis properties, applying the default version key prefix when absent.
         */
        public Redis {
            versionKeyPrefix = (versionKeyPrefix == null || versionKeyPrefix.isBlank())
                    ? "authz:tenant"
                    : versionKeyPrefix;
        }
    }

    /**
     * RabbitMQ integration configuration properties.
     *
     * @param enabled whether RabbitMQ event consumption is enabled
     * @param exchange topic exchange used for authorization events
     * @param queue queue consumed by the starter
     * @param routingKey routing key used to bind the queue
     */
    public record Rabbit(
            boolean enabled,
            String exchange,
            String queue,
            String routingKey
    ) {
        /**
         * Creates RabbitMQ properties, applying default exchange and routing key values when absent.
         */
        public Rabbit {
            exchange = (exchange == null || exchange.isBlank())
                    ? "authz.events"
                    : exchange;

            routingKey = (routingKey == null || routingKey.isBlank())
                    ? "authz.#"
                    : routingKey;
        }
    }

    /**
     * Observability configuration properties.
     *
     * @param logDecisions whether authorization decisions should be logged
     * @param logEvents whether consumed authorization events should be logged
     */
    public record Observability(
            boolean logDecisions,
            boolean logEvents
    ) {
    }
}
