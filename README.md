# RBAC Authz Spring Boot Starter

Starter Spring Boot para autorização RBAC multi-tenant.

Ele fornece um `AuthorizationService` simples para responder se um usuário pode executar uma permissão dentro de um tenant. As permissões são carregadas pela aplicação, armazenadas em cache local com Caffeine e podem ser invalidadas por versão de tenant usando Redis e eventos RabbitMQ.

## Quando usar

Use este starter quando a sua aplicação já possui usuários, papéis, tenants e permissões em seu próprio banco de dados, mas precisa de uma camada reutilizável para verificações como:

```java
authz.can(userId, tenantId, "workspace.members.read", currentUser);
```

Este projeto não define modelo de usuário, tabelas, claims JWT, papéis ou persistência. A aplicação consumidora continua dona dessas decisões e expõe o snapshot final de permissões implementando `PermissionSnapshotLoader`.

## Requisitos

- Java 21
- Spring Boot 4
- Maven
- Redis opcional
- RabbitMQ opcional

## Instalação

Adicione a dependência na aplicação consumidora:

```xml
<dependency>
    <groupId>dev.yuri</groupId>
    <artifactId>rbac-authz-spring-boot-starter</artifactId>
    <version>0.0.1</version>
</dependency>
```

## Uso básico

Implemente `PermissionSnapshotLoader` na aplicação que usa o starter:

```java
import dev.yuri.authzstarter.cache.PermissionSet;
import dev.yuri.authzstarter.snapshot.PermissionSnapshotLoader;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
class DatabasePermissionSnapshotLoader implements PermissionSnapshotLoader {

    @Override
    public PermissionSet load(UUID userId, UUID tenantId) {
        Set<String> permissions = Set.of(
                "workspace.read",
                "workspace.members.read"
        );

        return new PermissionSet(permissions);
    }
}
```

Depois injete `AuthorizationService` onde precisar tomar decisões de autorização:

```java
import dev.yuri.authzstarter.config.CurrentUser;
import dev.yuri.authzstarter.decision.AuthorizationService;

import java.util.UUID;

class WorkspaceService {

    private final AuthorizationService authz;

    WorkspaceService(AuthorizationService authz) {
        this.authz = authz;
    }

    boolean canReadMembers(UUID userId, UUID tenantId, CurrentUser currentUser) {
        return authz.can(userId, tenantId, "workspace.members.read", currentUser);
    }
}
```

O bean do serviço também é registrado com o nome `authz`.

## Configuração

Valores padrão:

```properties
authz.cache.l1-ttl=10m
authz.cache.version-refresh-interval=30s

authz.redis.enabled=true
authz.redis.version-key-prefix=authz:tenant

authz.rabbit.enabled=false
authz.rabbit.exchange=authz.events
authz.rabbit.routing-key=authz.#

authz.observability.log-decisions=false
authz.observability.log-events=false
```

A autorização principal funciona sem Redis e sem RabbitMQ. Sem Redis, as versões de permissão por tenant ficam em memória e começam em `0`.

## Como a decisão funciona

1. `AuthorizationService#can(...)` recebe `userId`, `tenantId`, a permissão desejada e o usuário atual.
2. Se `CurrentUser#isSystemAdmin()` for `true`, a decisão é permitida imediatamente.
3. O starter consulta a versão atual do tenant via `TenantPermissionVersionProvider`.
4. A chave do cache é formada por `tenantId`, `userId` e `version`.
5. Se não houver entrada em cache, `PermissionSnapshotLoader` é chamado para carregar as permissões.
6. A decisão final verifica se o `PermissionSet` contém a permissão solicitada.

## Versionamento com Redis

Quando existir um bean `StringRedisTemplate` e `authz.redis.enabled=true`, o starter usa Redis para ler a versão de permissões do tenant.

A chave lida segue o formato:

```text
{authz.redis.version-key-prefix}:{tenantId}:version
```

Exemplo:

```text
authz:tenant:ccfd9630-50f4-46f3-b2ae-51b26fb53fd6:version
```

Ao incrementar essa versão, a chave do cache muda e o próximo check carrega um novo snapshot de permissões.

## Invalidação com RabbitMQ

O suporte a RabbitMQ vem desativado por padrão. Habilite somente em aplicações que também tenham a infraestrutura Spring AMQP configurada:

```properties
authz.rabbit.enabled=true
authz.rabbit.exchange=authz.events
authz.rabbit.queue=my-service.authz-events
authz.rabbit.routing-key=authz.#
```

Eventos devem seguir o formato de `AuthzEvent`:

```java
new AuthzEvent(
        eventId,
        AuthzEventType.USER_ROLE_CHANGED,
        tenantId,
        nextVersion,
        occurredAt
);
```

Quando um evento chega com versão maior que a versão local do tenant, o starter atualiza a versão local e invalida o cache daquele tenant.

## Observabilidade

Ative logs de decisão e eventos quando precisar depurar o fluxo de autorização:

```properties
authz.observability.log-decisions=true
authz.observability.log-events=true
```

Com `log-decisions=true`, cada decisão registra resultado, permissão, usuário, tenant, versão, origem do cache e quantidade de permissões carregadas.

Com `log-events=true`, cada evento RabbitMQ registra se foi usado para invalidar o cache ou ignorado por não avançar a versão local.

## Desenvolvimento

Execute os testes:

```bash
./mvnw test
```

Compile e instale localmente:

```bash
./mvnw clean install
```

## Notas de design

- A autorização principal não depende de broker.
- Redis e RabbitMQ são camadas opcionais de infraestrutura.
- O carregamento das permissões fica sob responsabilidade da aplicação por meio de `PermissionSnapshotLoader`.
- Caffeine é usado como cache L1 em processo.
- `CurrentUser#isSystemAdmin()` permite bypass para fluxos de administração global.

## Licença

MIT
