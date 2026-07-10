# Tech Challenge - Fase 02

API REST desenvolvida com Java e Spring Boot para gerenciamento de usuários, restaurantes, tipos de usuário e itens de cardápio em um sistema de delivery compartilhado entre restaurantes.

---

# Contexto do Projeto

Um grupo de restaurantes decidiu contratar estudantes para construir um sistema de gestão compartilhado para seus estabelecimentos.

Ao invés de cada restaurante manter um sistema próprio, foi proposta uma plataforma única capaz de atender diferentes restaurantes, permitindo o gerenciamento de usuários, restaurantes e seus respectivos cardápios.

Nesta segunda fase, o projeto evoluiu para suportar múltiplos domínios da aplicação utilizando princípios de Clean Architecture.

---

# Tecnologias

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- Flyway
- Docker & Docker Compose
- MapStruct
- Lombok
- Spring Validation
- SpringDoc OpenAPI
- Maven
- H2 Database (testes)
- JaCoCo

---

# Arquitetura

O projeto foi reorganizado utilizando os princípios da **Clean Architecture**, separando responsabilidades entre domínio, casos de uso e infraestrutura.

```
application
 ├── dto
 ├── port
 └── usecase

domain
 ├── entity
 ├── repository
 └── exception

infrastructure
 ├── config
 ├── controller
 ├── persistence
 └── security
```

### Princípios aplicados

- SOLID
- Clean Code
- Clean Architecture
- Separation of Concerns
- Dependency Inversion

---

# Funcionalidades

## Usuários

- Cadastro
- Atualização
- Exclusão
- Busca por ID
- Busca paginada
- Alteração de senha
- Alteração do tipo de usuário

## Tipos de Usuário

- Cadastro
- Consulta
- Atualização
- Exclusão

## Restaurantes

- Cadastro
- Consulta
- Atualização
- Exclusão

## Cardápio

- Cadastro de itens
- Consulta
- Atualização
- Exclusão

## Segurança

- Autenticação JWT
- Endpoints protegidos
- Controle de acesso

---

# Banco de Dados

### PostgreSQL

Configuração padrão

- Host: localhost
- Porta: 5432
- Database: techfood
- Usuário: postgres
- Senha: admin

### Flyway

As migrations estão disponíveis em

```
src/main/resources/db/migration
```

### H2

Durante a execução dos testes é utilizado banco H2 em memória.


---

# Tratamento de Erros

A API utiliza Problem Details (RFC 7807) para padronização das respostas de erro.

---

# Como executar

## Docker

```bash
cd docker
docker-compose up --build
```

Aplicação disponível em

```
http://localhost:8080
```

## Execução local

```bash
mvn clean install
mvn spring-boot:run
```

---

# Postman

Uma collection do Postman foi disponibilizada para facilitar os testes dos endpoints da API.

```

postman/TechChallenge-Fase02.postman_collection.json

```
---

# Swagger

```
http://localhost:8080/swagger-ui/index.html
```

OpenAPI

```
http://localhost:8080/v3/api-docs
```

---

# Testes

Executar todos os testes

```bash
mvn test
```

Gerar relatório de cobertura do JaCoCo

```bash
mvn test jacoco:report
```

---

# Build

```bash
mvn clean install
```

---

# Grupo

- Lucas Walim da Silva
- Pamela Mendes Ribeiro
- Rafael Oliveira Rodrigues Valle
- Rodrigo Eufrásio Daniel
- Rodrigo Cavalcante de Barros

---

**Projeto desenvolvido na Pós-Tech em Arquitetura e Desenvolvimento Java pela FIAP.**
