package dev.yuri.authzstarter.config;

import dev.yuri.authzstarter.cache.PermissionCache;
import dev.yuri.authzstarter.cache.TenantPermissionVersionProvider;
import dev.yuri.authzstarter.messaging.AuthzEventListener;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for consuming authorization invalidation events from RabbitMQ.
 */
@AutoConfiguration(after = AuthzAutoConfiguration.class)
@EnableConfigurationProperties(AuthzProperties.class)
@ConditionalOnClass({RabbitListener.class, ConnectionFactory.class})
@ConditionalOnProperty(prefix = "authz.rabbit", name = "enabled", havingValue = "true")
public class AuthzRabbitAutoConfiguration {

    /**
     * Creates the RabbitMQ auto-configuration.
     */
    public AuthzRabbitAutoConfiguration() {
    }

    /**
     * Creates the authorization events topic exchange.
     *
     * @param properties authorization starter properties
     * @return topic exchange for authorization events
     */
    @Bean
    @ConditionalOnMissingBean
    public TopicExchange authzExchange(AuthzProperties properties) {
        return new TopicExchange(properties.rabbit().exchange(), true, false);
    }

    /**
     * Creates the authorization events queue when a queue name is configured.
     *
     * @param properties authorization starter properties
     * @return durable authorization events queue
     */
    @Bean
    @ConditionalOnProperty(prefix = "authz.rabbit", name = "queue")
    @ConditionalOnMissingBean
    public Queue authzQueue(AuthzProperties properties) {
        return QueueBuilder.durable(properties.rabbit().queue()).build();
    }

    /**
     * Binds the authorization queue to the authorization exchange.
     *
     * @param authzQueue authorization events queue
     * @param authzExchange authorization events exchange
     * @param properties authorization starter properties
     * @return binding between queue and exchange
     */
    @Bean
    @ConditionalOnProperty(prefix = "authz.rabbit", name = "queue")
    @ConditionalOnMissingBean
    public Binding authzBinding(Queue authzQueue, TopicExchange authzExchange, AuthzProperties properties) {
        return BindingBuilder.bind(authzQueue).to(authzExchange).with(properties.rabbit().routingKey());
    }

    /**
     * Creates the JSON message converter for RabbitMQ authorization events.
     *
     * @return JSON message converter
     */
    @Bean
    @ConditionalOnMissingBean
    public JacksonJsonMessageConverter authzJacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    /**
     * Creates the Rabbit listener container factory used by authorization events.
     *
     * @param connectionFactory RabbitMQ connection factory
     * @param converter JSON message converter
     * @return listener container factory
     */
    @Bean(name = "authzRabbitListenerContainerFactory")
    @ConditionalOnBean(ConnectionFactory.class)
    @ConditionalOnMissingBean(name = "authzRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory authzRabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter converter
    ) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setPrefetchCount(20);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(4);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    /**
     * Creates the event listener that invalidates local permission cache entries.
     *
     * @param versionProvider tenant permission version provider
     * @param cache permission cache
     * @param properties authorization starter properties
     * @return authorization event listener
     */
    @Bean
    @ConditionalOnProperty(prefix = "authz.rabbit", name = "queue")
    @ConditionalOnBean(
            value = {TenantPermissionVersionProvider.class, PermissionCache.class},
            name = "authzRabbitListenerContainerFactory"
    )
    @ConditionalOnMissingBean
    public AuthzEventListener authzEventListener(
            TenantPermissionVersionProvider versionProvider,
            PermissionCache cache,
            AuthzProperties properties
    ) {
        return new AuthzEventListener(versionProvider, cache, properties);
    }
}
