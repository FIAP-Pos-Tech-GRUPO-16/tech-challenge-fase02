# Planejamento da Fase 2 — Divisão de Trabalho do Grupo

Este documento define como o trabalho da Fase 2 será dividido entre os 5 membros do grupo, em fila sequencial (um membro só começa depois que o PR do anterior for mergeado na `main`), para minimizar conflitos de merge.

## Ponto de atenção

O CLAUDE.md exige nomes de classes/pacotes/endpoints em Português-BR, mas o código atual da Fase 1 está em inglês (`User`, `UserController`, `/v1/users`). Este roteiro assume que o refactor do Membro 1 vai renomear tudo para PT-BR (`Usuario`, `UsuarioController`, `/v1/usuarios`). Se isso não acontecer, os membros 2 a 5 precisam ajustar as referências de nome abaixo para bater com o que foi efetivamente entregue.

## Regra de ouro

Fila sequencial, um de cada vez. Ninguém cria branch antes do PR anterior estar mergeado na `main`, e todos rodam `git checkout main && git pull` antes de criar a própria branch.

---

## Membro 1 — Rodrigo Cavalcante de Barros (setup e gabarito)

Responsável pela etapa inicial, que define o padrão que todos os demais devem seguir:

- Criação/estruturação do repositório.
- Refactor da Fase 1 para Clean Architecture (`domain` / `application` / `infrastructure`).
- Configuração base do projeto e adição de dependências novas necessárias.
- Configuração do JaCoCo (gate de cobertura mínima de 80%).
- Remodelagem de Usuário/Tipo de Usuário: sai da herança JPA (`Customer`/`RestaurantOwner`) e vai para composição com tabela própria (`TipoUsuario` associado por FK).

Esta etapa precisa estar mergeada na `main` antes de o Membro 2 começar.

---

## Membro 2 — Domínio Tipo de Usuário

**Pré-requisito:** confirmar merge do PR do Membro 1. Verificar se já existe `TipoUsuario`/tabela `tipos_usuario` básica (criada pelo Membro 1 para o `Usuario` compilar) — se sim, completar; se não, criar do zero.

**Branch:** `feature/tipo-usuario`

**Estrutura:**
```
domain/entity/TipoUsuario.java
domain/repository/TipoUsuarioRepository.java
application/dto/{CriarTipoUsuarioRequest, AtualizarTipoUsuarioRequest, TipoUsuarioResponse, AssociarTipoUsuarioRequest}.java
application/usecase/{CriarTipoUsuarioUseCase, AtualizarTipoUsuarioUseCase, ExcluirTipoUsuarioUseCase, BuscarTipoUsuarioPorIdUseCase, ListarTiposUsuarioUseCase, AssociarTipoUsuarioAoUsuarioUseCase}.java
infrastructure/persistence/tipousuario/{TipoUsuarioJpaEntity, TipoUsuarioJpaRepository, TipoUsuarioRepositoryImpl, TipoUsuarioMapper}.java
infrastructure/controller/TipoUsuarioController.java
```

**Campos:** `TipoUsuario`: `id` (UUID), `nome` (String, obrigatório, único).

**Endpoints:**

| Método | Path | Códigos |
|---|---|---|
| POST | `/v1/tipos-usuario` | 201, 400, 409 |
| GET | `/v1/tipos-usuario` | 200 |
| GET | `/v1/tipos-usuario/{id}` | 200, 404 |
| PUT | `/v1/tipos-usuario/{id}` | 200, 400, 404, 409 |
| DELETE | `/v1/tipos-usuario/{id}` | 204, 404 |
| PATCH | `/v1/usuarios/{usuarioId}/tipo-usuario` | 200, 404 |

Reaproveitar `NoSuchElementException`/`ResourceAlreadyExistsException` já existentes (tratadas no `ControllerExceptionHandler`) em vez de criar exceções novas por domínio.

**Migration:** conferir último `Vn` real na `main` pós-merge antes de numerar a sua.

**Testes:** unitários de cada use case (mock dos repositórios) + `TipoUsuarioControllerIntegrationTest` (H2 + Flyway) cobrindo os 6 endpoints e erros.

**Commits (exemplos):**
```
feat: adiciona entidade e repositorio de dominio de tipo de usuario
feat: cria migration da tabela de tipos de usuario
feat: adiciona casos de uso de tipo de usuario
feat: adiciona controller rest de tipo de usuario
feat: adiciona endpoint de associacao de tipo ao usuario
test: cobre casos de uso e endpoints de tipo de usuario
docs: documenta endpoints de tipo de usuario no readme e postman
```

**PR:** título `feat: adiciona dominio de tipo de usuario`, base `main`, pedir revisão de 1 colega.

---

## Membro 3 — Domínio Restaurante

**Pré-requisito:** confirmar merge do Membro 2. Confirmar nome real da tabela/entidade de Usuário para a FK do dono.

**Branch:** `feature/restaurante`

**Estrutura:**
```
domain/entity/{Restaurante, Endereco}.java   (Endereco é um VO próprio deste domínio, não reaproveitar o de Usuário)
domain/repository/RestauranteRepository.java
application/dto/{CriarRestauranteRequest, AtualizarRestauranteRequest, RestauranteResponse, EnderecoDTO}.java
application/usecase/{CriarRestauranteUseCase, AtualizarRestauranteUseCase, ExcluirRestauranteUseCase, BuscarRestaurantePorIdUseCase, ListarRestaurantesUseCase}.java
infrastructure/persistence/restaurante/{RestauranteJpaEntity, RestauranteJpaRepository, RestauranteRepositoryImpl, RestauranteMapper}.java
infrastructure/controller/RestauranteController.java
```

**Campos:** `nome`, `endereco` (rua/número/cidade/cep), `tipoCozinha` (texto livre, não enum), `horarioFuncionamento` (texto livre), `donoId` (UUID — FK simples a nível de banco, sem `@ManyToOne` navegável entre agregados).

Não validar que o tipo do usuário seja "Dono de Restaurante" — fora do escopo pedido, só validar que o `donoId` existe.

**Endpoints:**

| Método | Path | Códigos |
|---|---|---|
| POST | `/v1/restaurantes` | 201, 400, 404 (dono não existe) |
| GET | `/v1/restaurantes` | 200 |
| GET | `/v1/restaurantes/{id}` | 200, 404 |
| PUT | `/v1/restaurantes/{id}` | 200, 400, 404 |
| DELETE | `/v1/restaurantes/{id}` | 204, 404 |

**Migration:** `V{n+1}__create_table_restaurantes.sql` com FK `dono_id UUID NOT NULL REFERENCES <tabela_usuario>(id)`.

**Testes:** unitários (incluindo caso "dono não existe") + `RestauranteControllerIntegrationTest`.

**Commits:**
```
feat: adiciona entidade e repositorio de dominio de restaurante
feat: cria migration da tabela de restaurantes
feat: adiciona casos de uso de restaurante
feat: adiciona controller rest de restaurante
test: cobre casos de uso e endpoints de restaurante
docs: documenta endpoints de restaurante no readme e postman
```

**PR:** título `feat: adiciona dominio de restaurante`.

---

## Membro 4 — Domínio Item de Cardápio

**Pré-requisito:** confirmar merge do Membro 3 (`Restaurante`/`RestauranteRepository` disponíveis).

**Branch:** `feature/item-cardapio`

**Estrutura:**
```
domain/entity/ItemCardapio.java
domain/repository/ItemCardapioRepository.java
application/dto/{CriarItemCardapioRequest, AtualizarItemCardapioRequest, ItemCardapioResponse}.java
application/usecase/{CriarItemCardapioUseCase, AtualizarItemCardapioUseCase, ExcluirItemCardapioUseCase, BuscarItemCardapioPorIdUseCase, ListarItensCardapioUseCase}.java
infrastructure/persistence/itemcardapio/{ItemCardapioJpaEntity, ItemCardapioJpaRepository, ItemCardapioRepositoryImpl, ItemCardapioMapper}.java
infrastructure/controller/ItemCardapioController.java
```

**Campos:** `nome`, `descricao`, `preco` (BigDecimal > 0), `disponivelApenasNoLocal` (boolean), `caminhoFoto` (String, só o caminho), `restauranteId` (UUID).

**Endpoints:**

| Método | Path | Códigos |
|---|---|---|
| POST | `/v1/itens-cardapio` | 201, 400, 404 (restaurante não existe) |
| GET | `/v1/itens-cardapio?restauranteId=...` | 200, 400 (se `restauranteId` ausente) |
| GET | `/v1/itens-cardapio/{id}` | 200, 404 |
| PUT | `/v1/itens-cardapio/{id}` | 200, 400, 404 |
| DELETE | `/v1/itens-cardapio/{id}` | 204, 404 |

**Migration:** `V{n+1}__create_table_itens_cardapio.sql` com FK `restaurante_id UUID NOT NULL REFERENCES restaurantes(id)`.

**Testes:** unitários (incluindo "restaurante não existe" e "preço inválido") + `ItemCardapioControllerIntegrationTest`.

**Commits:**
```
feat: adiciona entidade e repositorio de dominio de item de cardapio
feat: cria migration da tabela de itens de cardapio
feat: adiciona casos de uso de item de cardapio
feat: adiciona controller rest de item de cardapio
test: cobre casos de uso e endpoints de item de cardapio
docs: documenta endpoints de item de cardapio no readme e postman
```

**PR:** título `feat: adiciona dominio de item de cardapio`.

---

## Membro 5 — Consolidação (cobertura, Docker, docs, Postman, apoio ao vídeo)

**Pré-requisito:** confirmar merge do Membro 4 e que `mvn clean verify` passa na `main`.

**Branch:** `chore/consolidacao-fase2`

**Tarefas:**

- **Cobertura:** rodar `mvn clean verify`, abrir `target/site/jacoco/index.html`, escrever os testes que faltarem para bater 80% total (inclusive em código de outros membros, se necessário).
- **Teste ponta a ponta:** criar `FluxoCompletoIntegrationTest` cobrindo criar usuário → criar tipo → associar → criar restaurante → criar item de cardápio → listar.
- **Docker Compose:** subir `docker compose up --build`, validar que Flyway roda com as 4 tabelas e a API responde.
- **README:** consolidar arquitetura Clean Architecture, modelo de dados final (4 tabelas), tabela de endpoints completa (Auth + Usuários + Tipos de Usuário + Restaurantes + Itens de Cardápio), seção de grupo.
- **Postman:** revisar as 4 pastas, rodar a collection inteira via Runner na ordem lógica do fluxo, corrigir o que quebrar.
- **Apoio ao vídeo (~5min):** roteiro sugerido — contexto (30s) → arquitetura em camadas (30s) → demo Postman do fluxo completo (2-3min) → Swagger UI (30s) → `mvn clean verify` com testes verdes e cobertura ≥80% (30s) → encerramento.

**Commits:**
```
test: adiciona fluxo de integracao ponta a ponta entre os dominios
test: cobre lacunas de cobertura identificadas pelo jacoco
docs: consolida diagrama de arquitetura e modelo de dados no readme
docs: consolida endpoints e instrucoes de execucao no readme
chore: atualiza collection postman com fluxo completo de demonstracao
```

**PR:** título `chore: consolida testes, cobertura e documentacao da fase 2`. Depois desse merge, o grupo grava o vídeo final.