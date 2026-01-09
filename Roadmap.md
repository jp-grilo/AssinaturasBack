

# 🚀 Projeto 1: Sistema de Gestão de Assinaturas (Multi-tenant)

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
7. **Springdoc OpenAPI:** Documentação interativa (Swagger).

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
