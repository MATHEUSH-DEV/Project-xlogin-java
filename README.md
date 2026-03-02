# Kronus Rift Login Frontend (Java)

Este repositório contém o cliente de login/registro para o jogo **Kronus Rift**, implementado em Java com interface Swing e banco de dados SQLite. O objetivo deste documento é fornecer uma visão geral, orientar melhorias e servir como ponto de partida para futuras refatorações e expansão de funcionalidades.

---

## 🚀 Visão geral do sistema

- **Front-end**: Telas de login e cadastro desenvolvidas com Java Swing.
- **Banco de dados**: SQLite (`kronus_local.db`) armazenando informações de usuários.
- **Segurança**: Senhas hashadas com `BCrypt` (custo 12) através da classe `PasswordHasher`.
- **Integração**: `GameLauncher` executa o projeto .NET/C# `KronusLobby` usando `ProcessBuilder`.
- **Utilitários**: Scripts Python (`user_create.py`, `item_generator.py`) para manipulação externa do banco.

---

## 🧩 Arquitetura proposta

1. **Modularização**
   - Separar código em pacotes explícitos:
     - `ui` – componentes gráficos (login, cadastro, etc.)
     - `service` – lógica de negócio (autenticação, validação, etc.)
     - `dao` – acesso a dados com JDBC ou micro‑ORM
     - `model` – entidades de domínio (usuário, status, inventário)
     - `util` – ferramentas auxiliares (hashing, configuração, etc.)

   > ✅ Neste repositório já existe a estrutura de pacotes e as classes de exemplo
   (User, UserStatus, UserDao, AuthenticationService, RegistrationService, etc.),
   demonstrando a modularização proposta.

2. **Externalização de configurações**
   - Usar `.properties`, YAML ou variáveis de ambiente para:
     - Caminho do banco
     - Comando/ligações do jogo
     - Fator de custo do BCrypt
     - Portas, URLs e outros parâmetros mutáveis

3. **Camada de persistência**
   - Implementar DAOs para encapsular SQL.
   - Opcional: adotar um micro‑ORM como JDBI ou MyBatis para reduzir boilerplate.
   - Incluir suporte a migrações (Flyway, Liquibase) para versionar o esquema.

4. **Testes automatizados**
   - **Unitários** para:
     - `PasswordHasher` e seu método `checkPassword`.
     - Conversores/enumerações (`UserStatus.fromInt`).
     - Validações de entrada e regras de negócio.
   - **Integração** com banco em memória (`jdbc:sqlite::memory:`) para testar DAOs.
   - **UI** com ferramentas como AssertJ‑Swing caso aceitável.
   - Configurar **CI** para execuções automáticas (GitHub Actions, Azure Pipelines, etc.).

5. **Validação e tratamento de erros**
   - Validar campos (email, senha, nome de usuário) antes de persistir.
   - Exibir mensagens de erro amigáveis e logar detalhes em arquivo.
   - Gerenciar recursos com `try-with-resources` e garantir fechamento adequado.
   - Implementar políticas de bloqueio para múltiplas tentativas de login falhas.

6. **Empacotamento e build**
   - Migrar para **Maven** ou **Gradle** para gerenciar dependências (BCrypt, driver SQLite).
   - Configurar plugin para gerar JAR executável com dependências (fat JAR/uber JAR).

7. **Interface e experiência**
   - Considerar migração de Swing para **JavaFX** ou implementação de uma interface web leve (Spring Boot + Thymeleaf/React).
   - Adicionar progress indicators, placeholders consistentes e feedback visual.
   - Internacionalizar a UI (i18n) para pt‑BR e outros idiomas.

8. **Funcionalidades e integração futura**
   - Sincronização de status do usuário entre o front-end e o lobby .NET.
   - Lista de amigos e mensagens in‑game, usando tabelas adicionais no SQLite ou serviço remoto.
   - Inventário do jogador, possivelmente alimentado pelos scripts Python internos.
   - Expandir com APIs REST ou socket para comunicação em tempo real com o lobby.

9. **Documentação**
   - Adicionar README completo com instruções de instalação, configuração e execução.
   - Incluir exemplos de uso dos scripts Python e descrição do esquema do banco.
   - Manter changelog e notas de release.

10. **Outros aprimoramentos**
    - Garantir compatibilidade cross‑platform (Windows/macOS/Linux).
    - Monitorar e tratar concorrência no SQLite (uso de pool de conexões ou arquivo em rede).
    - Log centralizado (SLF4J + Logback ou similar).

---

## ✅ Resultado esperado

Ao aplicar estas sugestões, o projeto se tornará mais limpo, testável e fácil de manter. A modularização permitirá evolução em paralelo (por exemplo, trocar Swing pelo web) sem reescrever toda a lógica, e o uso de ferramentas de build/CI dará confiança e portabilidade. A introdução de migrações e configuração externa facilitará distribuição para diferentes máquinas.

Se você quiser continuar desenvolvendo, pense primeiro nesses passos arquiteturais e vá incrementando com pequenas iterações acompanhadas de testes.

---

*Este README fornece um norte completo e livre de erros para orientar a refatoração e expansão do seu projeto.*
