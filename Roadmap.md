

# Sistema de Gestão de Assinaturas (Multi-tenant)

## 1. Visão Geral e Arquitetura

Plataforma SaaS B2B para gestão de assinaturas. Múltiplas empresas (Tenants) compartilham a mesma aplicação, com dados isolados por tenant.

- **Arquitetura:** Monolito modular.
- **Multi-tenancy:** Coluna `tenant_id` + filtro de Hibernate e `TenantContext` para isolar dados.
- **Autenticação:** JWT + Spring Security; atualmente o `X-Tenant-ID` no header alimenta o `TenantContext` para otimização. Planejamento de migrar para tenantId apenas no claim do JWT.

## 2. Papéis e Usuários

- **Tenant (Empresa):** cliente do SaaS, dono dos planos e dados do seu escopo.
- **Usuário final (Client):** pessoa/usuário que assina planos dentro de um Tenant.
- **SUPER_ADMIN (GLOBAL_ADMIN):** administrador do SaaS, *sem tenant* (tenantId = null), tem acesso global e pode criar/manter tenants e usuários.

Decisões principais:
- Cada `User` é scoped por tenant (tem `tenantId`) — exceto `SUPER_ADMIN` que tem `tenantId = null`.
- Emails são únicos por tenant (constraint composta `(tenant_id, email)`), permitindo o mesmo email em tenants distintos.

## 3. Entidades Principais e Relações

- **Tenant**
    - id (UUID), name, slug, active, metadata
    - 1 Tenant → N Users
    - 1 Tenant → N Plans

- **User** (herda `BaseEntity` com `tenant_id`) — representa usuário pertencente a um tenant (exceto SUPER_ADMIN)
    - id, name, email, password(hashed), role (TENANT_ADMIN / CLIENT / GLOBAL_ADMIN)
    - Unique constraint: `(tenant_id, email)`

- **Plan** (tenant-scoped)
    - id, name, description, price, billing_cycle, active, tenant_id

- **Subscription** (nova entidade proposta)
    - id, user_id, plan_id, tenant_id (redundante, mas reforça isolamento), start_date, end_date, next_billing_date,
        status (TRIAL, ACTIVE, CANCELLED, PAST_DUE), price_snapshot, billing_cycle_snapshot, quantity, external_id
    - Regras: não permitir múltiplas assinaturas do mesmo user para o mesmo tenant/plan
    - Relações: User N..N Plan via Subscription (um usuário pode ter assinaturas em múltiplos tenants desde que existam contas separadas)

Notas de modelagem:
- `Plan` é tenant-scoped (pertence ao tenant). Se no futuro desejar planos globais, migrar para tabela global + tabela `tenant_plan`.
- `User` é scoped por tenant para simplificar RBAC e isolamento. Um mesmo indivíduo pode ter contas em múltiplos tenants (emails iguais em tenants diferentes).

## 4. Regras de Autenticação e TenantContext

- Fluxo atual:
    - Cliente envia `X-Tenant-ID` no header em requests (UUID). Esse header popula `TenantContext` via `TenantFilter`.
    - JWT é emitido no login e contém claims (incluindo tenantId). `JwtAuthenticationFilter` valida token e popula `SecurityContext`.
    - Precedência atual: `X-Tenant-ID` (header) possui prioridade para performance/filtragem; quando ausente, extrair tenantId do JWT.

- Recomendações de segurança / migração:
    - Migrar para tenantId exclusivamente no claim do JWT (reduz risco de spoofing). Durante migração, validar consistência header == claim quando ambos existirem.
    - Tokens curtos + refresh tokens para reduzir janela de comprometimento.

## 5. Regras de Negócio e Validações

- Ao criar recursos (plans, subscriptions, users) validar que `TenantContext.getCurrentTenant()` está presente e que o recurso pertence ao tenant.
- Apenas `SUPER_ADMIN` (tenantId = null) pode criar tenants e pode criar usuários para qualquer tenant.
- Quando header estiver ausente, permitir a operação apenas se o usuário autenticado for `SUPER_ADMIN`.
- Ao criar `Subscription`:
    - Verificar `plan.tenantId == user.tenantId == TenantContext`.
    - Não permitir duplicidade `user + plan` no mesmo tenant.
    - Salvar `price_snapshot` e `billing_cycle_snapshot` para consistência histórica.

## 6. Banco de Dados e Constraints

- H2 usado em dev; Flyway será adicionado antes de migrar para PostgreSQL.
- `BaseEntity` provê `tenant_id` e o filtro Hibernate é ativado por `TenantAspect`.
- Constraint: `UNIQUE(tenant_id, email)` na tabela `users`.
- `Plan` e `Subscription` estendem `BaseEntity` (filtráveis globalmente).

## 7. Checklist / Próximos Passos (curto prazo)

1. Implementar e registrar `TenantFilter` (na cadeia do Spring Security antes do JWT).  
2. Ajustar `User`: remover `unique=true` global e adicionar constraint composta `(tenant_id, email)`.  
3. Atualizar `JwtAuthenticationFilter`/`TokenService` para respeitar precedência header > claim e garantir que `TenantContext` seja populado.  
4. Criar entidade `Subscription`, DTOs, repositório e serviço (validações de tenant + snapshots).  
5. Ajustar `PlanService.create` para setar `tenantId` e `active = true`.  
6. Adicionar testes básicos e rodar localmente com H2.  

## 8. Decisões Arquiteturais e Racional

- Preferência por tenantId no JWT a médio prazo para reduzir superfície de ataque.  
- Manter `tenant_id` em todas entidades sensíveis e forçar validação server-side (não confiar em header enviado pelo cliente).  
- SUPER_ADMIN como conta global (tenantId null) para administração.

---
Arquivo gerado/atualizado com decisões até o momento.
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
