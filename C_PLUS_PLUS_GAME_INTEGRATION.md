# 🎮 Integração Java → C++ Game Engine

## Como Funciona Agora

### 1️⃣ Fluxo de Execução

```
Xlogin.java
    ↓
AuthenticationService.authenticate()
    ↓
GameLauncher.launchLobby(userId)
    ↓
CppLobbyWindow (exibe lobby em Java Swing)
    ↓
Usuário seleciona personagem
    ↓
CppLobbyWindow.enterGame(Character)
    ├─ Serializa Character → JSON
    ├─ Salva em: C:\Users\<user>\AppData\Local\Temp\kronus_char_<userId>.json
    └─ Executa: c_game\build\KronusRiftGame.exe
    ↓
main.cpp (C++)
    ├─ Lê JSON do TEMP
    ├─ Carrega stats do personagem
    └─ Inicia Game Engine
    ↓
Game.run() - Loop Principal
    ├─ 60 FPS
    ├─ Input (mouse click, hotkeys 1-4)
    ├─ Physics & Combat
    └─ Render (Raylib)
```

---

## 🛠️ Modificações Feitas

### 1. `CppLobbyWindow.java`
**Antes**: Abria GameWindow.java (Java Swing)
```java
// ❌ Antigo
game.GameWindow gameWindow = new game.GameWindow(ch, userId, this);
gameWindow.setVisible(true);
```

**Depois**: Abre executável C++
```java
// ✅ Novo
String characterJson = ch.toJSON();
// ... salva em arquivo temp ...
Process gameProcess = pb.start();
```

**Funcionalidades**:
- ✅ Procura `KronusRiftGame.exe` em múltiplos caminhos
- ✅ Serializa Character em JSON completo
- ✅ Passa dados via arquivo temporário
- ✅ Fecha lobby quando game inicia
- ✅ Limpa arquivo temp após game encerrar

### 2. `Character.java`
**Novo método**: `toJSON()`
```java
public String toJSON() {
    StringBuilder json = new StringBuilder();
    json.append("{\n");
    json.append("  \"name\": \"").append(name).append("\",\n");
    json.append("  \"race\": \"").append(race).append("\",\n");
    json.append("  \"class\": \"").append(clazz).append("\",\n");
    // ... stats, abilities, etc ...
    return json.toString();
}
```

**Formato JSON enviado**:
```json
{
  "name": "Herói",
  "race": "Humano",
  "class": "Guerreiro",
  "level": 1,
  "experience": 0,
  "stats": {
    "strength": 21,
    "agility": 10,
    "intelligence": 8
  },
  "health": 142,
  "mana": 66,
  "abilities": [
    {
      "name": "Golpe Poderoso",
      "description": "...",
      "manaCost": 20,
      "damageMultiplier": 1.5,
      "cooldownMs": 3000
    }
  ]
}
```

### 3. `main.cpp` (C++)
**Novo**: Carrega JSON do arquivo temporário
```cpp
std::string loadPlayerDataFromJava() {
    // Procura em: C:\Users\<user>\AppData\Local\Temp\kronus_char_*.json
    // Lê arquivo e retorna JSON
    // Se não encontrar, usa dummy data
}
```

### 4. `AssetManager.hpp`
**Correção**: Removido include desnecessário
```cpp
// ❌ Antes
#include <glm/glm.hpp>

// ✅ Depois
#include <memory>
#include <string>
#include <fstream>
```

---

## 📁 Caminhos de Busca do Executável

CppLobbyWindow procura em ordem:
1. `c_game/build/KronusRiftGame.exe` (padrão)
2. `./c_game/build/KronusRiftGame.exe`
3. `../c_game/build/KronusRiftGame.exe`
4. `KronusRiftGame.exe` (PATH do sistema)

Se nenhum for encontrado, mostra erro com instruções de build.

---

## 🏗️ Build do C++ Game

### Windows (PowerShell)
```powershell
cd c_game
mkdir build
cd build
cmake ..
cmake --build . --config Release
```

### Linux/macOS
```bash
cd c_game
mkdir -p build
cd build
cmake ..
cmake --build . --config Release
```

### CMake vai:
- ✅ Baixar Raylib 4.5.0 automaticamente
- ✅ Baixar nlohmann/json automaticamente
- ✅ Compilar todo o código C++
- ✅ Gerar executável `KronusRiftGame.exe` (Windows) ou `KronusRiftGame` (Linux/Mac)

---

## 🧪 Teste o Fluxo Completo

### 1. Compilar C++
```powershell
cd c:\Users\08422402173\Documents\xlogin-project-java\Project-xlogin-java\c_game
mkdir build
cd build
cmake ..
cmake --build . --config Release
```

**Resultado esperado**:
```
[100%] Built target KronusRiftGame
```

### 2. Executar Java
```bash
java -jar seu_projeto.jar
```
Ou via IDE (VS Code, IntelliJ, etc)

### 3. Fazer Login
1. Clique em **Entrar**
2. Use credenciais válidas
3. Lobby abre em Java Swing

### 4. Criar/Selecionar Personagem
1. Preencha nome, raça, classe
2. Clique em **Criar Personagem**
3. Clique em **Entrar no Jogo**

### 5. Verificar Que Game C++ Abriu
- ✅ Janela "Kronus Rift - Floresta de Eldoria" aparecer
- ✅ Mostrar mapa em pixel art
- ✅ Mostrar stats do personagem no HUD
- ✅ Aceitar input do mouse (click-to-move)
- ✅ Aceitar hotkeys (1, 2, 3, 4)

---

## 🐛 Troubleshooting

### Problema: "Executável do jogo não encontrado!"

**Solução**:
```bash
# Verifique se o arquivo existe
ls c_game/build/KronusRiftGame.exe

# Se não existir, compile:
cd c_game/build
cmake --build . --config Release
```

### Problema: Game abre mas não mostra personagem

**Causa**: Arquivo JSON não foi criado corretamente

**Verificação**:
```bash
# Verifique arquivo temp (Windows)
dir %TEMP%\kronus_char_*.json

# Verifique arquivo temp (Linux/Mac)
ls /tmp/kronus_char_*.json
```

**Solução**:
- Verifique que `Character.toJSON()` implementado corretamente
- Verifique encoding UTF-8 do JSON

### Problema: "TEMP directory not found"

**Solução**: Se em Windows, certifique-se que variável `TEMP` existe:
```powershell
$env:TEMP
```

Deve retornar caminho como: `C:\Users\<user>\AppData\Local\Temp`

---

## 📊 Dados Transmitidos

### De Java para C++
```
Character (serializado como JSON)
├── name, race, class
├── level, experience
├── stats (STR, AGI, INT)
├── health, mana
└── abilities[] (nome, cooldown, mana, dano)
```

### De C++ para Java (futuro)
```
GameResult (após terminar sessão)
├── experienceGained
├── enemiesDefeated
├── finalStats (STR, AGI, INT, Level)
├── abilitiesUsed
└── timePlayedSeconds
```

---

## 🔒 Segurança

- ✅ JSON criado em pasta TEMP privada do usuário
- ✅ Arquivo deletado após game encerrar
- ✅ Executável verificado para existência antes de executar
- ✅ Erros capturados e exibidos ao usuário

---

## 📈 Próximos Passos

1. **Testar fluxo completo** de login até game rodar
2. **Implementar REST API** no Java para sincronizar stats após jogo
3. **Melhorar loading** de assets (sprites, mapa, etc)
4. **Implementar networking** em tempo real (multiplayer)

---

## 📞 Referências

- **Documento**: `c_game/README_GAME_ENGINE.md` - Detalhes técnicos do engine
- **Build Guide**: `BUILD_DEPLOYMENT_GUIDE.md` - Como compilar
- **Java Integration**: `GUIA_INTEGRACAO_JAVA_CPP.md` - APIs e endpoints
- **Quick Start**: `START_HERE.md` - Comece aqui

---

**Status**: ✅ Integração Java → C++ Completa  
**Data**: 4 de Março de 2026  
**Próximo**: Compilar e testar!

