# Planejamento da Fase 2 — Divisão de Trabalho do Grupo

## Status atual

✅ Setup inicial, Refatoração para Clean Architecture, Configuração de Jacoco, Reescrita de testes Unitários, Ajsutes de Docker. 
✅ Domínio de Tipo de Usuário completo, CRUD completo

As seções abaixo foram atualizadas com os nomes reais de classes, pacotes, endpoints e migrations efetivamente entregues em cada etapa. Os membros do grupo devem usar essas referências.

## Regra de ouro

Fila sequencial, um de cada vez. Ninguém cria branch antes do PR anterior estar mergeado na `main`, e todos rodam `git checkout main && git pull` antes de criar a própria branch.

---

## 1. Setup Inicial — Rodrigo Cavalcante de Barros: ✅ ENTREGUE

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

## 2. Domínio Tipo de Usuário — Rodrigo Cavalcante de Barros: ✅ ENTREGUE

Branch: `feature/tipo-usuario` (9 commits atômicos, ainda não mergeada/aberta como PR). `mvn clean verify`: **104 testes passando, cobertura de linhas 96,72%**, gate do JaCoCo em 80% passando.

### Estrutura real de pacotes

```
domain/
  entity/       TipoUsuario (já existia), Usuario ganhou o método associarTipo(UUID)
  repository/   TipoUsuarioRepository (interface estendida com salvar, existePorNome, excluirPorId, listarPaginado)
application/
  dto/                      CriarTipoUsuarioRequest, AtualizarTipoUsuarioRequest, TipoUsuarioResponse, AssociarTipoUsuarioRequest
  usecase/tipousuario/      CriarTipoUsuarioUseCase, AtualizarTipoUsuarioUseCase, ExcluirTipoUsuarioUseCase,
                            BuscarTipoUsuarioPorIdUseCase, ListarTiposUsuarioUseCase, TipoUsuarioResponseFactory
  usecase/usuario/          AssociarTipoUsuarioAoUsuarioUseCase (fica junto de usuario, pois quem é lido/mutado/salvo é o agregado Usuario)
infrastructure/
  persistence/tipousuario/  TipoUsuarioJpaRepository e TipoUsuarioRepositoryImpl completados (entidade JPA e mapper já existiam prontos)
  controller/               TipoUsuarioController (novo); UsuarioController ganhou o endpoint de associação
```

### Endpoints reais

| Método | Path | Códigos |
|---|---|---|
| POST | `/v1/tipos-usuario` | 201, 400, 409 |
| GET | `/v1/tipos-usuario` | 200 (paginado) |
| GET | `/v1/tipos-usuario/{id}` | 200, 404 |
| PUT | `/v1/tipos-usuario/{id}` | 200, 400, 404, 409 |
| DELETE | `/v1/tipos-usuario/{id}` | 204, 404 |
| PATCH | `/v1/usuarios/{usuarioId}/tipo-usuario` | 200, 404 (no `UsuarioController`, não no `TipoUsuarioController`) |

### Decisões e pontos de atenção para os próximos membros

- **Nenhuma migration nova foi criada** — o índice único `uk_tipos_usuario_nome` já existia desde `V4__create_table_tipos_usuario.sql`. Última migration continua sendo `V8__finalize_users_tipo_usuario_id.sql`; o Membro 3 começa a numerar a partir de `V9`.
- Reaproveitadas `java.util.NoSuchElementException`/`RecursoJaExistenteException` (não foram criadas exceções novas por domínio) — sigam o mesmo padrão para Restaurante e Item de Cardápio.
- Padrão de use case por pacote de agregado (`usecase/<agregado>/`) confirmado como convenção do projeto — Restaurante e Item de Cardápio devem seguir o mesmo layout (`usecase/restaurante/`, `usecase/itemcardapio/`), não um pacote plano.
- Continua sem atualização de README ou Postman — ambos ficam para o Membro 5.

**PR:** título `feat: adiciona dominio de tipo de usuario`, base `main`, pedir revisão de 1 colega.

---

## 3. Domínio Restaurante: 🕢PENDENTE

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

## 4. Domínio Item de Cardápio: 🕢PENDENTE

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

## 5. Consolidação (cobertura, Docker, docs, Postman, apoio ao vídeo): 🕢PENDENTE

**Pré-requisito:** confirmar merge do Membro 4 e que `mvn clean verify` passa na `main`.

**Branch:** `chore/consolidacao-fase2`

**Tarefas:**

- **Cobertura:** rodar `mvn clean verify`, abrir `target/site/jacoco/index.html`, escrever os testes que faltarem para bater 80% total (inclusive em código de outros membros, se necessário).
- **Teste ponta a ponta:** criar `FluxoCompletoIntegrationTest` cobrindo criar usuário → criar tipo → associar → criar restaurante → criar item de cardápio → listar.
- **Docker Compose:** subir `docker compose up --build`, validar que Flyway roda com as 4 tabelas e a API responde.
- **README:** o README está propositalmente mínimo desde o início da Fase 2 (só instruções de execução) — escrever do zero as seções de arquitetura Clean Architecture, modelo de dados final (4 tabelas), tabela de endpoints completa (Auth + Usuários + Tipos de Usuário + Restaurantes + Itens de Cardápio) e seção de grupo.
- **Postman:** a collection foi removida no início da Fase 2 — criar uma nova collection do zero (`postman/TechChallengeFase02-postman_collection.json`) cobrindo os 4 domínios, com uma pasta por domínio, variáveis de ambiente (`baseUrl`, `token`) e rodar via Runner na ordem lógica do fluxo antes de considerar pronta.
- **Apoio ao vídeo (~5min):** roteiro sugerido — contexto (30s) → arquitetura em camadas (30s) → demo Postman do fluxo completo (2-3min) → Swagger UI (30s) → `mvn clean verify` com testes verdes e cobertura ≥80% (30s) → encerramento.
