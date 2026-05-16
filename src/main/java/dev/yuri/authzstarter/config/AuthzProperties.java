package dev.yuri.authzstarter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "authz")
public record AuthzProperties(
        Cache cache,
        Redis redis,
        Rabbit rabbit,
        Observability observability
) {

    public AuthzProperties {
        cache = (cache == null) ? new Cache(null, null) : cache;
        redis = (redis == null) ? new Redis(true, null) : redis;
        rabbit = (rabbit == null) ? new Rabbit(false, null, null, null) : rabbit;
        observability = (observability == null) ? new Observability(false, false) : observability;
    }

    public record Cache(
            Duration l1Ttl,
            Duration versionRefreshInterval
    ) {
        public Cache {
            l1Ttl = (l1Ttl == null) ? Duration.ofMinutes(10) : l1Ttl;
            versionRefreshInterval = (versionRefreshInterval == null) ? Duration.ofSeconds(30) : versionRefreshInterval;
        }
    }

    public record Redis(
            boolean enabled,
            String versionKeyPrefix
    ) {
        public Redis {
            versionKeyPrefix = (versionKeyPrefix == null || versionKeyPrefix.isBlank())
                    ? "authz:tenant"
                    : versionKeyPrefix;
        }
    }

    public record Rabbit(
            boolean enabled,
            String exchange,
            String queue,
            String routingKey
    ) {
        public Rabbit {
            exchange = (exchange == null || exchange.isBlank())
                    ? "authz.events"
                    : exchange;

            routingKey = (routingKey == null || routingKey.isBlank())
                    ? "authz.#"
                    : routingKey;
        }
    }

    public record Observability(
            boolean logDecisions,
            boolean logEvents
    ) {
    }
}
