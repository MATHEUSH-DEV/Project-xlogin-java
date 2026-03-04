# 🎮 Kronus Rift - Game Engine C++ (Raylib)

## 📋 Visão Geral

Game engine profissional em **C++17 + Raylib** para escalar o Kronus Rift de um jogo texto Java para um **MOBA-style RPG em pixel art** estilo WoW.

### Arquitetura
```
┌─────────────────────────────────────────────────┐
│        KRONUS RIFT GAME ENGINE (C++)             │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌──────────────┐      ┌──────────────┐        │
│  │ Raylib (2D)  │      │ Input System │        │
│  │ Renderer     │      │ (Mouse/Keys) │        │
│  └──────────────┘      └──────────────┘        │
│                                                 │
│  ┌──────────────┐      ┌──────────────┐        │
│  │ Game World   │      │ Combat System│        │
│  │ (Tiles)      │      │ (A*, AI)     │        │
│  └──────────────┘      └──────────────┘        │
│                                                 │
│  ┌──────────────────────────────────┐          │
│  │ Network Client (REST API)        │          │
│  │ ↔ Java Backend (Auth + Persist)  │          │
│  └──────────────────────────────────┘          │
│                                                 │
└─────────────────────────────────────────────────┘
```

---

## 🚀 Features Implementadas

### ✅ Player System
- **Stats**: STR, AGI, INT (sincronizados com Java)
- **Leveling**: Level-up automático a cada 1000 XP
- **Abilities**: 4 habilidades por classe (Guerreiro, Caçador, Bruxo)
- **Hotkeys**: `1,2,3,4` para usar abilities
- **Movimento**: Click-to-move (estilo LoL/Dota)

### ✅ Combat System
- **Dano**: `STR + (AGI/2)` - fórmula do Java
- **Ability Damage**: `BaseDamage × DamageMultiplier`
- **Cooldowns**: Sistema de cooldown por habilidade
- **Mana System**: Custo de mana por ability

### ✅ Enemy System
- **Tipos**: Goblin, Lobo, Espírito Florestal, Bandido
- **Wave Spawning**: Automático a cada 10s
- **AI**: Perseguem e atacam o jogador
- **XP Reward**: Ganho de experiência ao eliminar

### ✅ UI/HUD
- **Left Panel**: Stats (Level, HP, Mana, STR/AGI/INT)
- **Right Panel**: Abilities com cooldowns
- **Bottom Panel**: Combat log
- **Progress Bars**: HP e Mana

### ✅ Network Integration
- **REST API Client**: Pronto para conectar com Java backend
- **Character Serialization**: JSON ↔ C++ objects
- **Auto-save**: Sincroniza com servidor periodicamente

---

## 📦 Estrutura de Pastas

```
c_game/
├── CMakeLists.txt                 # Build config
├── include/                        # Headers
│   ├── entities/
│   │   ├── Player.hpp
│   │   ├── Enemy.hpp
│   │   └── Ability.hpp
│   ├── systems/
│   │   ├── InputHandler.hpp
│   │   ├── CombatSystem.hpp
│   │   ├── PathFinding.hpp
│   │   └── NetworkClient.hpp
│   ├── game/
│   │   ├── Game.hpp
│   │   ├── GameWorld.hpp
│   │   └── GameState.hpp
│   └── ui/
│       ├── UIManager.hpp
│       └── HUD.hpp
├── src/                            # Implementações
│   ├── main.cpp
│   ├── entities/
│   ├── systems/
│   ├── game/
│   └── ui/
└── res/                            # Assets (sprites, maps)
    ├── sprites/
    ├── maps/
    └── fonts/
```

---

## 🛠️ Como Compilar

### Pré-requisitos
- CMake 3.20+
- C++ compiler com suporte C++17 (MSVC, GCC, Clang)
- Windows/Linux/macOS

### Build
```bash
cd c_game
mkdir build
cd build
cmake ..
cmake --build . --config Release
```

### Executar
```bash
./KronusRiftGame
```

---

## 🎮 Controles

| Ação | Controle |
|------|----------|
| **Mover** | Clique do mouse |
| **Ability 1** | Tecla `1` |
| **Ability 2** | Tecla `2` |
| **Ability 3** | Tecla `3` |
| **Ability 4** | Tecla `4` |
| **Descansar (heal)** | Tecla `R` |
| **Inventário** | Tecla `I` |
| **Toggle HUD** | Tecla `U` |
| **Pausa** | Espaço |
| **Sair** | ESC |

---

## 📡 Integração com Java Backend

### REST API Endpoints (Implementar em Java)

```http
POST   /api/auth/login
GET    /api/character/{name}
PUT    /api/character/{name}
POST   /api/combat/report
PATCH  /api/character/{name}/sync
```

### Exemplo de Sincronização
```cpp
// C++ envia dados ao Java
networkClient->syncPlayerStats(
    player->level,      // 5
    player->health,     // 120
    player->mana,       // 80
    player->experience  // 2500
);
```

---

## 🎨 Sistema de Classes

### Guerreiro
- **Stats Bonus**: +3 STR, -2 INT
- **Abilities**:
  1. Golpe Poderoso (150% dano, 20 mana, 3s CD)
  2. Fúria Berserker (200% dano, 50 mana, 10s CD)
  3. Defesa Heroica (50% redução dano, 30 mana, 5s CD)
  4. Investida (120% dano + knockback, 25 mana, 4s CD)

### Caçador
- **Stats Bonus**: +4 AGI, -2 STR
- **Abilities**:
  1. Tiro Preciso (120% dano, 15 mana, 2s CD)
  2. Chuva de Flechas (140% dano, 30 mana, 5s CD)
  3. Camuflar (invisível, 0 dano, 20 mana, 8s CD)
  4. Disparos Automáticos (100% dano, 25 mana, 3s CD)

### Bruxo
- **Stats Bonus**: +5 INT, -4 STR
- **Abilities**:
  1. Bola de Fogo (170% dano, 25 mana, 2.5s CD)
  2. Maldição Sombria (190% dano, 40 mana, 8s CD)
  3. Escudo de Mana (100 absorção, 35 mana, 6s CD)
  4. Explosão Arcana (180% dano AoE, 45 mana, 7s CD)

---

## 🔄 Sistema de Combate

### Fórmula de Dano
```
DanoBasico = STR + (AGI / 2)
DanoAbility = DanoBasico × DamageMultiplier
DanoMitigado = Dano - (AGI_inimigo / 5)
```

### Exemplo (Guerreiro Level 1)
```
STR: 21 (18 + 3 bonus)
AGI: 12
Dano = 21 + (12/2) = 27 dano base

Golpe Poderoso:
Dano = 27 × 1.5 = 40 dano
Custo: 20 mana
Cooldown: 3s
```

---

## 📊 Roadmap

### MVP (Atual)
- [x] Player movement + abilities
- [x] Enemy spawning + combat
- [x] HUD + stats display
- [x] Audio framework

### Phase 2
- [ ] Multiple worlds (maps)
- [ ] Boss encounters
- [ ] Item system + inventory
- [ ] Multiplayer (LAN)
- [ ] Quest system

### Phase 3
- [ ] Dungeon instances
- [ ] Guilds/clans
- [ ] Trading system
- [ ] PvP arenas
- [ ] Leaderboards

---

## 🐛 Troubleshooting

### Erro: `raylib not found`
```bash
# Instalar raylib globalmente
cmake --install build --prefix ~/.local
```

### Erro de link: `unresolved external symbol`
Certifique-se que `nlohmann_json` foi instalado via FetchContent.

### Frame drops
Reduza `TILES_Y` em `GameWorld.hpp` ou aumente `targetFPS` em `Game.hpp`.

---

## 📚 Referências

- **Raylib**: https://www.raylib.com/
- **glm**: https://glm.g-truc.net/
- **nlohmann/json**: https://github.com/nlohmann/json
- **Game Engine Design**: https://www.gamedev.net/

---

## 🤝 Contribuir

Para adicionar features:

1. Criar branch: `git checkout -b feature/nova-feature`
2. Implementar com comentários em português/inglês
3. Testar em múltiplas plataformas
4. Fazer PR com descrição clara

---

## 📄 Licença

Kronus Rift © 2026 - Todos os direitos reservados

---

**Desenvolvido com ❤️ em C++17 + Raylib**
