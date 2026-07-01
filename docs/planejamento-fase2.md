# Planejamento da Fase 2 — Divisão de Trabalho do Grupo

Este documento define como o trabalho da Fase 2 será dividido entre os 5 membros do grupo, em fila sequencial (um membro só começa depois que o PR do anterior for mergeado na `main`), para minimizar conflitos de merge.

## Status atual

A etapa do Membro 1 (gabarito de Clean Architecture) **já foi implementada** na branch `feature/clean-architecture-usuario` (ainda não mergeada/aberta como PR). A seção "Membro 1" abaixo foi atualizada com os nomes reais de classes, pacotes, endpoints e migrations efetivamente entregues — os Membros 2 a 5 devem usar essas referências (não as genéricas descritas originalmente).

## Regra de ouro

Fila sequencial, um de cada vez. Ninguém cria branch antes do PR anterior estar mergeado na `main`, e todos rodam `git checkout main && git pull` antes de criar a própria branch.

---

## Membro 1 — Rodrigo Cavalcante de Barros (setup e gabarito) — ENTREGUE

Branch: `feature/clean-architecture-usuario` (9 commits atômicos, `mvn clean verify` com 72 testes passando e cobertura de linhas 93,11%, gate do JaCoCo em 80% configurado e passando).

### Estrutura real de pacotes

```
domain/
  entity/       Usuario, TipoUsuario, Endereco
  repository/   UsuarioRepository, TipoUsuarioRepository, ResultadoPaginado<T>
  exception/    RegraDeNegocioException, RecursoJaExistenteException, CredenciaisInvalidasException
application/
  usecase/usuario/       CriarUsuarioUseCase, AtualizarUsuarioUseCase, BuscarUsuarioPorIdUseCase,
                         BuscarUsuariosPorNomeUseCase, ExcluirUsuarioUseCase, UsuarioResponseFactory
  usecase/autenticacao/  AutenticarUsuarioUseCase, AlterarSenhaUseCase
  dto/          CriarUsuarioRequest, AtualizarUsuarioRequest, UsuarioResponse, EnderecoDTO,
                LoginRequest, TokenResponse, AlterarSenhaRequest, Pagina<T>
  port/         CodificadorDeSenha, GeradorDeToken (interfaces que isolam a Application de BCrypt/JJWT)
infrastructure/
  persistence/usuario/      UsuarioJpaEntity, EnderecoJpaEmbeddable, UsuarioJpaRepository,
                             UsuarioRepositoryImpl, UsuarioMapper
  persistence/tipousuario/  TipoUsuarioJpaEntity, TipoUsuarioJpaRepository, TipoUsuarioRepositoryImpl,
                             TipoUsuarioMapper  (apenas o necessário para Usuario funcionar — CRUD completo é do Membro 2)
  controller/    UsuarioController, AutenticacaoController, controller/handler/ControllerExceptionHandler,
                 controller/response/{ApiSuccessResponse, Meta, ValidationError}
  security/      JwtService (implements GeradorDeToken), JwtAuthenticationFilter, JwtAuthenticationEntryPoint,
                 UsuarioDetailsServiceImpl (implements UserDetailsService), CodificadorDeSenhaBCrypt (implements CodificadorDeSenha)
  config/        SecurityConfig, PasswordConfig, OpenApiConfig
```

### Decisões e pontos de atenção para os próximos membros

- **Tabela `users` mantém o nome em inglês no banco** (só as classes Java e endpoints mudaram para PT-BR) — ao criar FK de `donoId`/`restaurante` para usuário, referencie `users(id)`.
- **`TipoUsuario`**: já existe `domain.entity.TipoUsuario`, `domain.repository.TipoUsuarioRepository` (com `buscarPorId`/`existePorId`) e a persistência mínima em `infrastructure.persistence.tipousuario`. O Membro 2 **completa** isso (CRUD completo + controller), não recria do zero.
- **Tipos seedados** (migration `V5__insert_tipos_usuario.sql`) com UUIDs fixos e previsíveis: `11111111-1111-1111-1111-111111111111` = "Cliente", `22222222-2222-2222-2222-222222222222` = "Dono de Restaurante".
- **Última migration usada:** `V8__finalize_users_tipo_usuario_id.sql`. O Membro 2 deve começar a numerar a partir de `V9`.
- **`Usuario.tipoUsuarioId`** é uma referência simples por UUID (sem navegação de objeto entre agregados) — mesmo padrão que Restaurante deve usar para `donoId` (Membro 3) e Item de Cardápio para `restauranteId` (Membro 4).
- **Exceções de domínio reutilizáveis:** `RecursoJaExistenteException` (409) e `java.util.NoSuchElementException` (404, tratado genericamente) — já mapeadas em `infrastructure/controller/handler/ControllerExceptionHandler`. Não criem uma exceção nova por domínio para esses dois casos.
- **Endpoints de autenticação:** login em `POST /v1/autenticacao/login` (não `/v1/auth/login`); troca de senha em `PUT /v1/usuarios/{id}/senha`.
- **JWT:** claims agora são `usuarioId` e `tipoUsuarioId` (strings). Nenhum controle de acesso por tipo de usuário foi implementado — está fora de escopo da Fase 2.
- **README simplificado de propósito:** o README completo (arquitetura, modelo de dados, endpoints, diagramas) foi removido para não gerar confusão com referências à Fase 1 durante o desenvolvimento paralelo. Ficou só um resumo mínimo de como rodar o projeto localmente. A documentação completa é consolidada pelo Membro 5 ao final, quando os 4 domínios já estiverem prontos.
- **Collection Postman removida** (`postman/TechChallengeFase02-postman_collection.json` não existe mais no repositório). Ninguém dos Membros 2, 3 e 4 precisa criar ou manter uma collection própria durante o desenvolvimento — a collection final é criada do zero pelo Membro 5, cobrindo os 4 domínios já integrados.
- Esta etapa precisa estar mergeada na `main` antes de o Membro 2 começar.

---

## Membro 2 — Domínio Tipo de Usuário

**Pré-requisito:** confirmar merge do PR do Membro 1. `TipoUsuario`/tabela `tipos_usuario` já existem (criados pelo Membro 1 para o `Usuario` compilar) — complete o que falta, não recrie do zero. Reaproveite `domain.entity.TipoUsuario`, `domain.repository.TipoUsuarioRepository` e a persistência em `infrastructure.persistence.tipousuario` (`TipoUsuarioJpaEntity`, `TipoUsuarioJpaRepository`, `TipoUsuarioRepositoryImpl`, `TipoUsuarioMapper`) já entregues.

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

Reaproveitar `java.util.NoSuchElementException`/`RecursoJaExistenteException` (pacote `domain.exception`) já existentes (tratadas no `infrastructure.controller.handler.ControllerExceptionHandler`) em vez de criar exceções novas por domínio.

**Migration:** a última usada pelo Membro 1 foi `V8__finalize_users_tipo_usuario_id.sql` — comece em `V9`, mas confira o último `Vn` real na `main` pós-merge antes de numerar a sua, caso algo tenha mudado.

**Testes:** unitários de cada use case (mock dos repositórios) + `TipoUsuarioControllerIntegrationTest` (H2 + Flyway) cobrindo os 6 endpoints e erros.

**Commits (exemplos):**
```
feat: adiciona entidade e repositorio de dominio de tipo de usuario
feat: cria migration da tabela de tipos de usuario
feat: adiciona casos de uso de tipo de usuario
feat: adiciona controller rest de tipo de usuario
feat: adiciona endpoint de associacao de tipo ao usuario
test: cobre casos de uso e endpoints de tipo de usuario
```

Não é necessário atualizar README ou Postman nesta etapa — o README está propositalmente mínimo até a consolidação final (Membro 5), e a collection Postman é recriada do zero por ele ao final.

**PR:** título `feat: adiciona dominio de tipo de usuario`, base `main`, pedir revisão de 1 colega.

---

## Membro 3 — Domínio Restaurante

**Pré-requisito:** confirmar merge do Membro 2. A tabela de usuário no banco chama-se `users` (o Membro 1 manteve o nome original em inglês na tabela, só as classes Java e endpoints viraram PT-BR) — use `REFERENCES users(id)` na FK do dono.

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

**Migration:** `V{n+1}__create_table_restaurantes.sql` com FK `dono_id UUID NOT NULL REFERENCES users(id)`.

**Testes:** unitários (incluindo caso "dono não existe") + `RestauranteControllerIntegrationTest`.

**Commits:**
```
feat: adiciona entidade e repositorio de dominio de restaurante
feat: cria migration da tabela de restaurantes
feat: adiciona casos de uso de restaurante
feat: adiciona controller rest de restaurante
test: cobre casos de uso e endpoints de restaurante
```

Não é necessário atualizar README ou Postman nesta etapa — fica a cargo do Membro 5 na consolidação final.

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
```

Não é necessário atualizar README ou Postman nesta etapa — fica a cargo do Membro 5 na consolidação final.

**PR:** título `feat: adiciona dominio de item de cardapio`.

---

## Membro 5 — Consolidação (cobertura, Docker, docs, Postman, apoio ao vídeo)

**Pré-requisito:** confirmar merge do Membro 4 e que `mvn clean verify` passa na `main`.

**Branch:** `chore/consolidacao-fase2`

**Tarefas:**

- **Cobertura:** rodar `mvn clean verify`, abrir `target/site/jacoco/index.html`, escrever os testes que faltarem para bater 80% total (inclusive em código de outros membros, se necessário).
- **Teste ponta a ponta:** criar `FluxoCompletoIntegrationTest` cobrindo criar usuário → criar tipo → associar → criar restaurante → criar item de cardápio → listar.
- **Docker Compose:** subir `docker compose up --build`, validar que Flyway roda com as 4 tabelas e a API responde.
- **README:** o README está propositalmente mínimo desde o início da Fase 2 (só instruções de execução) — escrever do zero as seções de arquitetura Clean Architecture, modelo de dados final (4 tabelas), tabela de endpoints completa (Auth + Usuários + Tipos de Usuário + Restaurantes + Itens de Cardápio) e seção de grupo.
- **Postman:** a collection foi removida no início da Fase 2 — criar uma nova collection do zero (`postman/TechChallengeFase02-postman_collection.json`) cobrindo os 4 domínios, com uma pasta por domínio, variáveis de ambiente (`baseUrl`, `token`) e rodar via Runner na ordem lógica do fluxo antes de considerar pronta.
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