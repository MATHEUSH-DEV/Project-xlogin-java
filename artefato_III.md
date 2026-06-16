## Artefato III — Banco de Dados

Entrega completa com: Análise de requisitos, DER (3FN), Modelo lógico (3FN), Dicionário de dados e scripts SQL (DDL + DML) prontos para MySQL Workbench.

### 1) Introdução
Este artefato descreve o modelo de dados do sistema XLogin (parte servidor/jogo leve) e inclui o script SQL pronto para ser colado no MySQL Workbench e executado sem erros.

> Observações / Assunções
- Foram extraídos os modelos de domínio a partir das classes Java presentes no repositório (`User`, `Session`, `LoginEvent`, `Character`, `Ability`).
- Inserimos campos de identidade (UUID) como `CHAR(36)` e usamos tipos MySQL compatíveis (DATETIME, INT, BIGINT, DOUBLE, TEXT).
- Assumi que cada `Character` pertence a um `User` (adicionada a FK `user_id` em `characters`). As `Ability` são instâncias de personagem (FK `character_id`).

### 2) Análise de Requisitos (resumo técnico)
- R1: Armazenar usuários com autenticação segura (hash de senha), status e timestamps.
- R2: Registrar sessões ativas (token JWT, expiração, IP/agent).
- R3: Auditar eventos de login (tentativas, sucesso/fracasso) com retenção de 90 dias.
- R4: Persistir personagens do jogo com atributos (STR/AGI/INT/vida/mana/exp/level).
- R5: Persistir habilidades (abilities) vinculadas ao personagem.
- R6: Permitir criação e consultas sem dados inconsistentes (Integridade referencial).

### 3) DER (normalizado até 3FN) — descrição textual
- Entidades principais: USERS, SESSIONS, LOGIN_EVENTS, CHARACTERS, ABILITIES.
- Relacionamentos:
  - Um `User` pode ter várias `Sessions` (1:N).
  - Um `User` pode ter vários `Characters` (1:N).
  - Um `Character` possui várias `Abilities` (1:N).
  - `LoginEvent` referencia `User` opcionalmente (N:1) — eventos de falha podem não ter user_id.

Justificativa 3FN: cada tabela armazena atributos que dependem unicamente da chave primária; não há grupos repetitivos (foi transformado em tabelas separadas); não há dependências transitivas entre colunas não-chave.

### 4) Modelo Lógico (tabelas e chaves)
- `users` (id PK CHAR(36))
- `sessions` (token PK VARCHAR(512), user_id FK -> users.id)
- `login_events` (id PK CHAR(36), user_id FK nullable -> users.id)
- `characters` (id PK CHAR(36), user_id FK -> users.id)
- `abilities` (id PK CHAR(36), character_id FK -> characters.id)

### 5) Dicionário de Dados (resumo por tabela)
- users
  - id: CHAR(36) PK, UUID do usuário
  - username: VARCHAR(20) NOT NULL UNIQUE
  - email: VARCHAR(255) NOT NULL UNIQUE
  - password_hash: VARCHAR(255) NOT NULL
  - status: ENUM('ACTIVE','SUSPENDED','DELETED') NOT NULL DEFAULT 'ACTIVE'
  - created_at, updated_at, last_login_at: DATETIME
  - failed_login_attempts: INT
  - failed_login_reset_at: DATETIME

- sessions
  - token: VARCHAR(512) PK (JWT)
  - user_id: CHAR(36) FK -> users.id
  - created_at, expires_at, revoked_at: DATETIME
  - ip_address, user_agent: strings

- login_events
  - id: CHAR(36) PK
  - user_id: CHAR(36) FK nullable
  - username, ip_address, user_agent: strings
  - success: BOOLEAN
  - failure_reason: VARCHAR
  - attempted_at, created_at, expires_at: DATETIME
  - duration_ms: INT

- characters
  - id: CHAR(36) PK
  - user_id: CHAR(36) FK -> users.id
  - name, race, class: strings
  - created_at: BIGINT (epoch ms)
  - level, strength, agility, intelligence, experience, health, mana: numerics

- abilities
  - id: CHAR(36) PK
  - character_id: CHAR(36) FK -> characters.id
  - name, description
  - mana_cost: INT
  - damage_multiplier: DOUBLE
  - cooldown_ms, last_used_time: BIGINT

### 6) Scripts SQL
O script SQL completo está no arquivo `sql/ddl_dml_mysql.sql`. Ele cria database, tabelas e realiza ao menos duas inserções (INSERT) em cada tabela não-associativa (users, sessions, login_events, characters, abilities).

### 7) Como executar
1) Abra o MySQL Workbench.
2) Copie todo o conteúdo do arquivo `sql/ddl_dml_mysql.sql` e cole em uma nova aba SQL.
3) Execute. O script cria o banco `xlogin` e insere dados de exemplo.

### 8) Observações finais
- Se desejar eu adapto o DER em imagem (SVG/PNG) com base nesse modelo.
- Se houver alterações no ER desejadas (por exemplo tornar `abilities` compartilhadas entre personagens), atualizo o modelo lógico e os scripts.

---
Arquivo SQL: `sql/ddl_dml_mysql.sql`
