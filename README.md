# Tech Challenge - Fase 02

API REST desenvolvida com Java e Spring Boot para gerenciamento de usuários, restaurantes e cardápios em um sistema de delivery compartilhado entre restaurantes.

> Este projeto está em desenvolvimento (Fase 2 do Tech Challenge). A documentação completa (arquitetura, modelo de dados, endpoints e collection Postman) será consolidada ao final da entrega, quando todos os domínios estiverem prontos. Este README traz apenas o essencial para compilar e executar o projeto localmente.

---

## Tecnologias

- Java 21
- Spring Boot 3.4.2
- Spring Web / Spring Data JPA / Spring Security + JWT
- PostgreSQL
- Flyway (versionamento de schema)
- Docker & Docker Compose
- MapStruct / Lombok
- SpringDoc OpenAPI
- Maven

---

## Como executar o projeto

### Pré-requisitos

- Java 21 (JDK Temurin ou OpenJDK)
- Maven 3.8+
- PostgreSQL 15+ (para execução sem Docker) OU Docker

### Opção 1: Com Docker

Subir ambiente completo (aplicação + PostgreSQL):

```bash
cd docker
docker-compose up --build
```

A aplicação estará disponível em: http://localhost:8080

### Opção 2: Execução local

Certifique-se de que o PostgreSQL está rodando em `localhost:5432` com database `tech_challenge_fase02`, usuário `postgres` e senha `admin`. Depois:

```bash
mvn clean install
mvn spring-boot:run
```

A aplicação estará disponível em: http://localhost:8080

### Documentação Swagger

```
http://localhost:8080/swagger-ui/index.html
```

### Testes e cobertura

```bash
mvn clean verify
```

O relatório de cobertura fica em `target/site/jacoco/index.html`. O build falha se a cobertura de linhas cair abaixo de 80%.

---

## Grupo

- Lucas Walim da Silva
- Pamela Mendes Ribeiro
- Rafael Oliveira Rodrigues Valle
- Rodrigo Eufrásio Daniel
- Rodrigo Cavalcante de Barros

**Projeto desenvolvido na pós-tech em Arquitetura e Desenvolvimento Java pela FIAP.**
