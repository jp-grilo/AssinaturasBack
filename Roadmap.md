

# Sistema de Gestão de Assinaturas (Multi-tenant)

## 1. Visão Geral e Arquitetura

O objetivo é construir um sistema robusto de assinaturas onde múltiplas empresas (Tenants) podem gerenciar seus clientes e planos de forma isolada.

* **Arquitetura:** Monolito Modular (Modular Monolith).
* **Estratégia de Multi-tenancy:** Identificador de Coluna (`tenant_id`) em nível de banco de dados, utilizando filtros do Hibernate para garantir o isolamento.
* **Padrão de Comunicação:** Injeção de Interfaces entre módulos, evoluindo para Event-Driven (Spring Events) conforme a necessidade.

## 2. Stack Técnica (Backend)

* **Linguagem:** Java 21+
* **Framework:** Spring Boot 3.x
* **Gerenciador de Dependências:** Maven
* **Banco de Dados:** H2 (Desenvolvimento inicial) / PostgreSQL (Produção)
* **Migrações:** Flyway
* **Documentação:** Springdoc OpenAPI (Swagger)
* **Segurança:** Spring Security + JWT
* **Ferramentas:** Docker & Docker Compose (Futuro)

## 3. Estado Inicial do Projeto

O projeto foi inicializado com as seguintes dependências no `pom.xml`:

1. **Spring Web:** Para criação da API REST.
2. **Spring Data JPA:** Para persistência de dados.
3. **Spring Security:** Para autenticação e autorização (Configuração inicial).
4. **Lombok:** Para reduzir boilerplate code.
5. **Validation:** Bean Validation para entradas.
6. **Flyway Migration:** Gerenciamento de schema.
7. **Springdoc OpenAPI:** Documentaçã# 🚀 Projeto 1: Sistema de Gestão de Assinaturas (Multi-tenant)

## 1. Visão Geral e Arquitetura
O objetivo é construir um sistema de assinaturas multi-tenant, onde múltiplas empresas (Tenants) gerenciam clientes e planos de forma isolada.

- **Arquitetura:** Monolito Modular.
- **Multi-tenancy:** Coluna `tenant_id` + Hibernate Filter (`tenantFilter`) habilitado por AOP.
- **Comunicação interna:** Injeção de interfaces; eventos Spring como evolução.

## 2. Stack Técnica (Backend)
- **Java 21+**, **Spring Boot 3.x**, **Maven**
- **JPA/Hibernate**, **Spring Security + JWT**
- **H2 (dev)**, **PostgreSQL (prod)**, **Flyway** (a habilitar)
- **Springdoc OpenAPI**
- **Docker/Compose** (planejado)

## 3. Estado Atual do Código
- **Multi-tenancy base:** `BaseEntity` com `tenant_id` e `@Filter`; `TenantContext` (ThreadLocal) e `TenantAspect` habilitando filtro antes dos repositórios.
- **Filtro de Tenant:** `TenantFilter` criado, mas o parsing/registro do `X-Tenant-ID` está incompleto e ainda não está claro se foi adicionado à chain do Spring Security.
- **Segurança/JWT:** `TokenService`, `JwtAuthenticationFilter` e `SecurityConfig` existem, porém com trechos não implementados (`...`). Regras de autorização e ordem dos filtros precisam ser confirmadas.
- **Identidade:** Entidade `User` criada, controller e DTOs prontos; `UserService`/`AuthService` têm trechos não implementados. Campos de senha/role não estão na entidade (apenas em DTO), e o repositório está pronto.
- **Tenants:** Entidade e repositório prontos; não há controller/service nem seed/migration.
- **Planos:** Entidade, DTOs, service (create/list) e controller prontos. O `tenantId` é atribuído via `TenantContext` no service. Campo `active` é obrigatório, mas não está sendo definido na criação (pode falhar em runtime).
- **Infra:** `application.properties` configurado para H2 em memória, Flyway desabilitado, `ddl-auto=update`. `pom.xml` ainda não lista dependências (necessário completar).
- **Migrations:** Nenhuma migration criada (Flyway off).

## 4. Estrutura de Pastas
```
src/main/java/com/projeto/subscription/
├── modules/
│   ├── identity/ (controllers, services, model User, repository, DTOs)
│   ├── tenant/   (model Tenant, repository)
│   └── plan/     (controllers, services, model Plan, repository, DTOs)
└── shared/
    ├── config/ (Security/JWT - incompletos)
    ├── exception/ (handler global)
    └── tenant_context/ (TenantContext, TenantFilter, TenantAspect)
```

## 5. Checklist de Progresso

### Fase 1: Setup do Ambiente
- [x] Gerar o projeto no Spring Initializr.
- [x] Configurar `application.properties` para H2 e console H2.
- [ ] Configurar `docker-compose.yml` (PostgreSQL/Redis).
- [ ] Criar migrations Flyway (tenants, users, plans, etc.).
- [ ] Completar `pom.xml` com as dependências efetivamente usadas.

### Fase 2: Estratégia de Isolamento (Multi-tenancy)
- [x] Criar o `TenantContext` usando `ThreadLocal`.
- [ ] Implementar o filtro para capturar `X-Tenant-ID` (parsing UUID + set/clear no contexto) e garantir registro na cadeia de filtros.
- [x] Configurar `@Filter` do Hibernate via `BaseEntity` + `TenantAspect`.
- [ ] Garantir que todas as entidades multi-tenant estendam `BaseEntity` (User não usa `tenant_id` hoje).

### Fase 3: Módulo de Identidade & Auth
- [ ] Completar entidade `User` (senha, role, tenant_id) e repositórios.
- [ ] Implementar `UserService` (hash de senha, role default, tenant).
- [ ] Implementar `AuthService` (login, validação de senha, retorno de JWT com role/tenant).
- [ ] Finalizar `TokenService`, `JwtAuthenticationFilter` e `SecurityConfig` (regras de autorização, ordem dos filtros, stateless).
- [ ] Adicionar endpoints de registro se necessário.

### Fase 4: Planos e Cobrança
- [ ] Ajustar criação de plano para preencher `active` (default true) e validar tenant.
- [ ] CRUD completo de planos (update/delete/toggle active).
- [ ] Integração Stripe + webhooks (futuro).

## 6. Próximos Passos Imediatos
1. Completar `pom.xml` com starters Web, Security, JPA, Validation, Lombok, H2, JWT (jjwt-api/impl/jackson), Springdoc.
2. Finalizar `TokenService`, `JwtAuthenticationFilter` e `SecurityConfig`; registrar `TenantFilter` antes da chain de segurança (ou dentro dela) para popular `TenantContext`.
3. Ajustar `TenantFilter` para parsear `X-Tenant-ID` como UUID e limpar no `finally`.
4. Evoluir a entidade `User` (senha + role + tenant_id) e serviços de autenticação/usuário.
5. Criar migrations Flyway para `tenants`, `users`, `plans` e remover `ddl-auto` em seguida.
6. Corrigir `PlanService.create` para definir `active = true` e garantir validação de tenant.

## 7. Decisões de Design
- **Java/Spring Boot:** Ecossistema maduro, segurança nativa (Security/JWT).
- **Monolito Modular:** Menor complexidade de deploy, organização por domínios.
- **Discriminator (coluna `tenant_id` + filtro):** Simples e performático para SaaS multi-tenant.o interativa (Swagger).

A configuração atual utiliza banco **H2 em memória** para agilizar o desenvolvimento das regras de negócio.

---

## 4. Estrutura de Pastas (Modular)

A arquitetura segue o padrão de **Monolito Modular**. O código é dividido por domínios em `modules/` e recursos técnicos compartilhados em `shared/`.

```text
src/main/java/com/projeto/subscription/
├── modules/
│   ├── identity/                 # Gestão de usuários e acesso
│   │   ├── controller/           
│   │   ├── service/              
│   │   ├── model/                # Entidade User (id, name, email, password)
│   │   ├── repository/           
│   │   └── dto/                  
│   │
│   ├── tenant/                   # Gestão das Empresas (Tenants)
│   │   ├── model/                # Entidade Tenant
│   │
│   ├── plan/                     # Catálogo de Planos
│   │
│   └── billing/                  # Faturamento e Assinaturas
│
├── shared/                       # O "Coração Técnico" compartilhado
│   ├── config/                   # Beans (Security, Swagger)
│   ├── exception/                # Handler global
│   ├── tenant_context/           # Multi-tenancy (X-Tenant-ID via Filter/ThreadLocal)
│   └── util/                     
│
└── SubscriptionApplication.java  
```

---

## 5. Checklist de Progresso

### Fase 1: Setup do Ambiente
* [x] Gerar o projeto no Spring Initializr.
* [x] Configurar `application.properties` para banco H2 e Console H2.
* [ ] Configurar `docker-compose.yml` (PostgreSQL/Redis) - *Pendente para fase de produção*.
* [ ] Criar a primeira migration Flyway para a tabela de `tenants`.

### Fase 2: Estratégia de Isolamento (Multi-tenancy)
* [ ] Criar o `TenantContext` usando `ThreadLocal`.
* [ ] Implementar Filtro para capturar `X-Tenant-ID`.
* [ ] Configurar `@Filter` do Hibernate para isolamento automático.

### Fase 3: Módulo de Identidade & Auth
* [ ] Finalizar entidade `User` e criar Repositories.
* [ ] Implementar Service de Identidade (BCrypt/JWT).
* [ ] Configurar Spring Security.

### Fase 4: Planos e Cobrança
* [ ] CRUD de Planos.
* [ ] Integração com Stripe e Webhooks.

## 6. Decisões de Design (Justificativas para o Portfólio)

* **Por que Java/Spring Boot?** Pela maturidade do ecossistema e ferramentas de segurança nativas (Spring Security).
* **Por que Monolito Modular?** Reduz a complexidade de deploy em comparação a microserviços, mas mantém o código organizado e pronto para escalabilidade futura.
* **Por que Discriminator Column?** É a forma mais comum e performática de implementar multi-tenancy em aplicações SaaS de larga escala.

---
