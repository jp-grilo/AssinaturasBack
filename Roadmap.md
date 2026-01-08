

# 🚀 Projeto 1: Sistema de Gestão de Assinaturas (Multi-tenant)

## 1. Visão Geral e Arquitetura

O objetivo é construir um sistema robusto de assinaturas onde múltiplas empresas (Tenants) podem gerenciar seus clientes e planos de forma isolada.

* **Arquitetura:** Monolito Modular (Modular Monolith).
* **Estratégia de Multi-tenancy:** Identificador de Coluna (`tenant_id`) em nível de banco de dados, utilizando filtros do Hibernate para garantir o isolamento.
* **Padrão de Comunicação:** Injeção de Interfaces entre módulos, evoluindo para Event-Driven (Spring Events) conforme a necessidade.

## 2. Stack Técnica (Backend)

* **Linguagem:** Java 21+
* **Framework:** Spring Boot 3.x
* **Banco de Dados:** PostgreSQL
* **Migrações:** Flyway
* **Documentação:** Springdoc OpenAPI (Swagger)
* **Segurança:** Spring Security + JWT
* **Ferramentas:** Docker & Docker Compose

## 3. Inicialização do Projeto (Spring Initializr)

Acesse [start.spring.io](https://start.spring.io/) e selecione as seguintes dependências:

1. **Spring Web:** Para criação da API REST.
2. **Spring Data JPA:** Para persistência de dados.
3. **PostgreSQL Driver:** Driver de conexão com o banco.
4. **Flyway Migration:** Gerenciamento de versões do banco de dados.
5. **Validation:** Bean Validation para validação de dados de entrada.
6. **Lombok:** Para reduzir boilerplate code (Getters/Setters).
7. **Spring Security:** Para autenticação e autorização.
8. **Springdoc OpenAPI:** Para gerar o Swagger automaticamente.
9. **Spring Modulith (Opcional/Recomendado):** Para validar a estrutura modular.

---

## 4. Exemplo Estrutura de Pastas (Modular)

```text
src/main/java/com/projeto/subscription/
├── modules/
│   ├── identity/                 # Gestão de usuários e acesso
│   │   ├── controller/           # Endpoints de login, registro e perfil
│   │   ├── service/              # Lógica de BCrypt, geração de JWT
│   │   ├── model/                # Entidade User e Role
│   │   ├── repository/           # Interface JpaRepository
│   │   └── dto/                  # Request/Response (UserRequest, AuthResponse)
│   │
│   ├── tenant/                   # Gestão das Empresas (Tenants)
│   │   ├── controller/           # Endpoints para cadastrar/editar empresas
│   │   ├── service/              # Lógica de ativação/bloqueio de tenant
│   │   ├── model/                # Entidade Tenant
│   │   └── repository/           
│   │
│   ├── plan/                     # Catálogo de Planos
│   │   ├── controller/           # Listagem de planos para o público
│   │   ├── service/              # Regras de upgrade/downgrade de planos
│   │   ├── model/                # Entidade Plan e Feature
│   │   ├── repository/
│   │   └── dto/                  # PlanResponse
│   │
│   └── billing/                  # Faturamento e Assinaturas (O mais complexo)
│       ├── controller/           # Checkout e histórico de faturas
│       ├── service/              # Integração com Stripe e renovação
│       ├── model/                # Entidade Subscription e Invoice
│       ├── repository/
│       ├── client/               # Classes que chamam a API externa (StripeClient)
│       └── dto/                  # CheckoutRequest, WebhookEventDTO
│
├── shared/                       # O "Coração Técnico" compartilhado
│   ├── config/                   # Beans de configuração (Security, Swagger, CORS)
│   ├── exception/                # Handler global (@ControllerAdvice) e exceções customizadas
│   ├── tenant_context/           # Lógica do Multi-tenancy (Filtros, ThreadLocal)
│   ├── security/                 # Filtros JWT e UserDetailsService
│   └── util/                     # Classes utilitárias (Data, Formatação, Mappers)
│
└── SubscriptionApplication.java  # Classe principal do Spring Boot

```

---

## 5. Passo a Passo Inicial

### Fase 1: Setup do Ambiente

* [ ] Gerar o projeto no Spring Initializr.
* [ ] Configurar `docker-compose.yml` com a imagem do PostgreSQL e Redis.
* [ ] Configurar `application.yml` para conectar ao banco Docker.
* [ ] Criar a primeira migration Flyway para a tabela de `tenants`.

### Fase 2: O Coração do Sistema (Multi-tenancy)

* [ ] Criar o `TenantContext` (usando `ThreadLocal`) para armazenar o ID da empresa durante a requisição.
* [ ] Implementar um **Filter** ou **Interceptor** que captura o `X-Tenant-ID` do header e injeta no contexto.
* [ ] Configurar o `@Filter` do Hibernate para filtrar automaticamente as queries pelo `tenant_id`.

### Fase 3: Módulo de Identidade & Auth

* [ ] Implementar cadastro de usuários vinculados a um Tenant.
* [ ] Configurar Spring Security para validar JWT.

### Fase 4: Módulo de Planos e Cobrança

* [ ] Criar CRUD de planos.
* [ ] Integrar SDK do Stripe para criação de `Checkout Sessions`.
* [ ] Criar endpoint de Webhook para processar confirmações de pagamento.

---

## 6. Decisões de Design (Justificativas para o Portfólio)

* **Por que Java/Spring Boot?** Pela maturidade do ecossistema e ferramentas de segurança nativas (Spring Security).
* **Por que Monolito Modular?** Reduz a complexidade de deploy em comparação a microserviços, mas mantém o código organizado e pronto para escalabilidade futura.
* **Por que Discriminator Column?** É a forma mais comum e performática de implementar multi-tenancy em aplicações SaaS de larga escala.

---
