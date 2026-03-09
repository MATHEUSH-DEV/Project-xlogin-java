# 🔗 Guia de Integração: Java Backend ↔ C++ Game Engine

## Visão Geral

O Kronus Rift é um sistema **híbrido** onde:
- **Java** = Autenticação, banco de dados, persistência, lógica de negócio
- **C++** = Renderização, gameplay em tempo real, input, física

Eles se comunicam via **REST API HTTP**.

---

## 1️⃣ Fluxo de Autenticação

```
Java Login                C++ Engine
    │                         │
    ├─ Usuário faz login      │
    │                         │
    ├─ Carrega personagem ────→ Envia JSON do character
    │                         │
    ├─────────────────────────→ NetworkClient faz GET /api/character/{name}
    │                         │
    ├─ Responde JSON ←────────┤
    │                         │
    │                         └─ Deserializa em Player object
    │                         │
    │                         └─ Inicia GameWorld
```

### Implementar em `AuthenticationService.java`

```java
@PostMapping("/api/character/{name}")
public ResponseEntity<String> getCharacter(@PathVariable String name) {
    Character character = characterRepository.findByName(name);
    if (character == null) {
        return ResponseEntity.notFound().build();
    }
    
    String jsonData = character.toJSON();  // Converte para JSON
    return ResponseEntity.ok(jsonData);
}
```

---

## 2️⃣ Sincronização de Stats

Durante o jogo, o C++ periodicamente envia atualizações para o Java.

```cpp
// C++ (a cada 30 segundos)
struct PlayerStats {
    int level;
    int health;
    int mana;
    int experience;
    int enemiesDefeated;
};

networkClient->syncPlayerStats(
    player->level,
    player->health,
    player->mana,
    player->experience
);
```

### Implementar em `GameLauncher.java`

```java
@PatchMapping("/api/character/{name}/sync")
public ResponseEntity<Void> syncStats(
        @PathVariable String name,
        @RequestBody PlayerStatsDTO stats) {
    
    Character character = characterRepository.findByName(name);
    character.setLevel(stats.level);
    character.setHealth(stats.health);
    character.setMana(stats.mana);
    character.setExperience(stats.experience);
    
    characterRepository.save(character);
    return ResponseEntity.ok().build();
}
```

---

## 3️⃣ Estrutura de Serialização JSON

### Character JSON (Java → C++)

```json
{
  "name": "Aragorn",
  "race": "Humano",
  "clazz": "Guerreiro",
  "level": 5,
  "experience": 2500,
  "strength": 28,
  "agility": 13,
  "intelligence": 8,
  "health": 156,
  "mana": 66,
  "maxHealth": 156,
  "maxMana": 66,
  "enemiesDefeated": 42,
  "position": {"x": 400.0, "y": 300.0}
}
```

### Combat Log (C++ → Java)

```json
{
  "characterName": "Aragorn",
  "combatData": {
    "enemyDefeated": "Goblin",
    "damageDealt": 150,
    "victory": true,
    "xpGained": 100,
    "timestamp": 1709559600000
  }
}
```

---

## 4️⃣ Endpoints REST Recomendados

### Autenticação
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "player1",
  "password": "secure123"
}

Response:
{
  "userId": 1,
  "token": "jwt_token_here",
  "sessionId": "sess_12345"
}
```

### Carregamento de Personagem
```http
GET /api/character/Aragorn?userId=1
Authorization: Bearer jwt_token_here

Response:
{
  "name": "Aragorn",
  "race": "Humano",
  "clazz": "Guerreiro",
  ...
}
```

### Sincronização de Stats
```http
PATCH /api/character/Aragorn/sync?userId=1
Authorization: Bearer jwt_token_here
Content-Type: application/json

{
  "level": 6,
  "health": 140,
  "mana": 70,
  "experience": 3200,
  "enemiesDefeated": 45
}
```

### Relatório de Combate
```http
POST /api/combat/report?characterName=Aragorn
Authorization: Bearer jwt_token_here
Content-Type: application/json

{
  "enemyName": "Goblin",
  "damageDealt": 150,
  "victory": true,
  "xpGained": 100
}
```

---

## 5️⃣ Configuração do Backend Java

### `application.properties`

```properties
# Server
server.port=8080
server.servlet.context-path=/api

# Database
spring.datasource.url=jdbc:sqlite:kronus_local.db
spring.jpa.database-platform=org.hibernate.dialect.SQLiteDialect

# CORS (permitir C++ client)
server.cors.allowed-origins=http://localhost:*
server.cors.allowed-methods=GET,POST,PUT,PATCH,DELETE
```

### `pom.xml` (dependências)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
</dependency>

<dependency>
    <groupId>org.mindrot</groupId>
    <artifactId>jbcrypt</artifactId>
    <version>0.4</version>
</dependency>
```

---

## 6️⃣ Implementação do Cliente REST (C++)

### `NetworkClient.cpp`

```cpp
#include "systems/NetworkClient.hpp"
#include <curl/curl.h>
#include <nlohmann/json.hpp>

using json = nlohmann::json;

// Callback para response
size_t WriteCallback(void* contents, size_t size, size_t nmemb, std::string* userp) {
    userp->append((char*)contents, size * nmemb);
    return size * nmemb;
}

std::string NetworkClient::performRequest(const std::string& method,
                                         const std::string& endpoint,
                                         const std::string& body) {
    CURL* curl = curl_easy_init();
    if (!curl) return "{}";
    
    std::string url = serverUrl + endpoint;
    std::string readBuffer;
    
    curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
    curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, method.c_str());
    curl_easy_setopt(curl, CURLOPT_POSTFIELDS, body.c_str());
    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, &readBuffer);
    
    // Header
    struct curl_slist* headers = NULL;
    headers = curl_slist_append(headers, "Content-Type: application/json");
    headers = curl_slist_append(headers, ("Authorization: Bearer " + sessionToken).c_str());
    curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);
    
    CURLcode res = curl_easy_perform(curl);
    
    curl_slist_free_all(headers);
    curl_easy_cleanup(curl);
    
    return readBuffer;
}

bool NetworkClient::syncPlayerStats(int level, int hp, int mana, int xp) {
    json payload;
    payload["level"] = level;
    payload["health"] = hp;
    payload["mana"] = mana;
    payload["experience"] = xp;
    
    std::string response = performRequest(
        "PATCH",
        "/api/character/Heroi/sync",
        payload.dump()
    );
    
    return !response.empty();
}
```

---

## 7️⃣ Fluxo Completo de Gameplay

```
1. Java Login
   └─→ Usuário faz login no Swing UI
       └─→ GameLauncher inicia C++ exe

2. C++ Game Starts
   └─→ main.cpp chama NetworkClient::loadCharacter()
       └─→ HTTP GET /api/character/{name}
           └─→ Recebe JSON
               └─→ Player::loadFromJSON()

3. Gameplay Loop
   └─→ C++ Game::run()
       └─→ CombatSystem::update()
           └─→ Inimigos spawnam, jogador ataca
               └─→ A cada 30s: networkClient->syncPlayerStats()

4. Jogador Morre ou Sai
   └─→ Game::shutdown()
       └─→ Sincroniza stats finais
           └─→ Volta para Java Lobby
```

---

## 8️⃣ Tratamento de Erros

### C++ Network Error Handler

```cpp
try {
    std::string response = networkClient->loadCharacter("Aragorn");
    auto data = json::parse(response);
    player->loadFromJSON(data.dump());
} catch (const std::exception& e) {
    // Fallback: usar último character salvo localmente
    std::ifstream localFile("./characters/Aragorn.json");
    if (localFile.is_open()) {
        std::string localJson((std::istreambuf_iterator<char>(localFile)),
                             std::istreambuf_iterator<char>());
        player->loadFromJSON(localJson);
    }
}
```

---

## 🔐 Segurança

1. **JWT Token**: Incluir em todo request para `/api/*`
2. **HTTPS em Produção**: Usar HTTPS, não HTTP
3. **Rate Limiting**: Limitar sincronização a 1x por 30s
4. **Input Validation**: Validar JSON no Java antes de salvar
5. **Log de Transactions**: Registrar todos os sync de stats

---

## 📝 Exemplo de Implementação Completa

### Java (Spring Boot)

```java
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GameController {
    
    @Autowired
    private CharacterService characterService;
    
    @GetMapping("/character/{name}")
    public ResponseEntity<String> getCharacter(@PathVariable String name) {
        Character ch = characterService.findByName(name);
        return ResponseEntity.ok(ch.toJSON());
    }
    
    @PatchMapping("/character/{name}/sync")
    public ResponseEntity<Void> syncStats(
            @PathVariable String name,
            @RequestBody Map<String, Integer> stats) {
        characterService.updateStats(name, stats);
        return ResponseEntity.ok().build();
    }
}
```

### C++

```cpp
// main.cpp
int main() {
    Game game("Kronus Rift", 1600, 900);
    
    // Carregar do Java
    NetworkClient client("http://localhost:8080");
    std::string jsonData = client.loadCharacter("Aragorn");
    
    // Iniciar
    game.initialize(jsonData);
    game.run();
    
    // Sincronizar ao sair
    game.shutdown();
    
    return 0;
}
```

---

## ✅ Checklist de Implementação

- [ ] Criar endpoints REST em `GameController.java`
- [ ] Implementar `NetworkClient.cpp` com curl
- [ ] Testar comunicação HTTP (Postman)
- [ ] Adicionar JWT authentication
- [ ] Implementar fallback offline (cache local)
- [ ] Configurar CORS
- [ ] Testar em múltiplas máquinas
- [ ] Documentar API (Swagger)

---

**Status**: Pronto para integração! 🚀
