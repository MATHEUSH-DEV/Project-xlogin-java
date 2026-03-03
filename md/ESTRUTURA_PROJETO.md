# Estrutura do Projeto - Kronus Rift

## 📂 Organização de Arquivos (Atualizada)

```
Project-xlogin-java/
│
├── src/
│   ├── model/
│   │   ├── User.java              (conta do jogador)
│   │   ├── UserStatus.java
│   │   └── Character.java         ⭐ NOVO - Stats (Level, STR, AGI, INT)
│   │
│   ├── dao/
│   │   └── UserDao.java
│   │
│   ├── service/
│   │   ├── AuthenticationService.java
│   │   ├── GameLauncher.java
│   │   └── RegistrationService.java
│   │
│   ├── ui/
│   │   ├── Xlogin.java            (login inicial)
│   │   ├── Xregister.java         (registro)
│   │   └── CppLobbyWindow.java    (lobby melhorado com game launch)
│   │
│   ├── game/
│   │   ├── GameWorld.java         ⭐ NOVO - Controla o mundo (Floresta)
│   │   └── GameWindow.java        ⭐ NOVO - Interface do jogo
│   │
│   └── util/
│       ├── PasswordHasher.java
│       ├── UIUtils.java
│       └── CharacterManager.java  ⭐ NOVO - Persistência JSON
│
├── out/                           (compilados .class)
│
├── lib/                           (dependências)
│   ├── jbcrypt-0.4.jar
│   ├── slf4j-api-2.0.9.jar
│   ├── slf4j-simple-2.0.9.jar
│   └── sqlite-jdbc-3.45.1.0.jar
│
├── res/
│   ├── sombra.gif                 (asset original)
│   ├── README_ASSETS.md           ⭐ NOVO - Guia para pixel art
│   ├── characters/                ⭐ NOVO - Pasta para sprites
│   │   ├── humano/
│   │   │   ├── idle.png
│   │   │   ├── walk.png
│   │   │   └── attack.png
│   │   ├── goblin/
│   │   └── elfo/
│   ├── enemies/                   ⭐ NOVO - Sprites dos inimigos
│   │   ├── goblin_enemy.png
│   │   ├── wolf.png
│   │   ├── forest_spirit.png
│   │   └── bandit.png
│   └── environments/              ⭐ NOVO - Backgrounds/tilesets
│       ├── forest_bg.png
│       └── grass_tileset.png
│
├── characters/                    (JSON local de personagens)
│   ├── user_4_characters.json
│   └── user_5_characters.json
│
├── c_lobby/
│   ├── CMakeLists.txt
│   ├── lobby.exe                  (executável C++)
│   └── src/
│       └── main.cpp
│
├── scripts/
│   ├── admin_tool.py
│   ├── item_generator.py
│   └── user_create.py
│
├── Project-xlogin-java.sln
├── README.md
├── GAME_SYSTEM_README.md          ⭐ NOVO - Documentação do sistema
└── kronus_local.db                (database SQLite)
```

## 🎯 Fluxo da Aplicação

```
┌─────────────────────────┐
│   Xlogin.java           │ ← Tela de Login
│  (Login/Registro)       │
└────────────┬────────────┘
             │ (autenticado)
             ↓
┌─────────────────────────────────────┐
│   CppLobbyWindow.java               │ ← Lobby (Nova versão melhorada)
│  ├─ Criar Personagem                │
│  │  ├─ Nome                         │
│  │  ├─ Raça (Humano/Goblin/Elfo)    │
│  │  └─ Classe (Caçador/Guerreiro/Bruxo)
│  │       ↓ (salva em JSON)          │
│  └─ Listar Personagens              │
│     ├─ Botão [Entrar] → GameWindow  │
│     └─ Botão [Deletar]              │
└────────────┬──────────────────────────┘
             │ (clica "Entrar")
             ↓
┌─────────────────────────────────────┐
│   GameWindow.java                   │ ← Jogo (Novo!)
│  ├─ Canvas 2D (Pixel Art)           │
│  │  └─ Espaço para tilesets/sprites │
│  ├─ Stats em Tempo Real             │
│  │  ├─ Level (1-∞)                  │
│  │  ├─ Health/Mana bars             │
│  │  ├─ STR/AGI/INT                  │
│  │  └─ Inimigos Derrotados          │
│  ├─ Ações                           │
│  │  ├─ Lutar (ganha XP)             │
│  │  ├─ Descansar (cura)             │
│  │  └─ Sair (volta ao lobby)        │
│  └─ GameWorld.java (lógica)         │
│     ├─ Combate (STR vs inimigo)    │
│     ├─ Experience (levelup auto)    │
│     └─ Mundo: Floresta de Eldoria   │
└─────────────────────────────────────┘
```

## 💾 Estrutura de Dados

### Character (JSON + Memory)
```json
{
  "name": "Aragorn",
  "race": "Elfo",
  "clazz": "Guerreiro",
  "level": 5,
  "strength": 28,
  "agility": 17,
  "intelligence": 20,
  "health": 156,
  "mana": 140,
  "experience": 5500,
  "createdAt": 1741027843500
}
```

### Arquivo: `/characters/user_4_characters.json`
```json
[
  { ... character1 ... },
  { ... character2 ... },
  { ... character3 ... }
]
```

## 🎨 Assets Esperados (Pixel Art)

### Resolução & Tamanho
- **Personagens**: 32×48 pixels (corpo + cabeça)
- **Inimigos**: 32×32 ou 32×48 pixels
- **Tilesets**: 32×32 pixels por tile
- **Backgrounds**: 640×480 pixels (ou maior com scroll)

### Cores Recomendadas
- Paleta limitada (16-256 cores)
- Transparência (alpha channel)
- Sem anti-aliasing (crisp pixels)

### Estrutura de Sprite Sheet
```
[Idle Frame 1] [Idle Frame 2] [Idle Frame 3]
[Walk Frame 1] [Walk Frame 2] [Walk Frame 3]
[Attack 1]     [Attack 2]     [Attack 3]
```

## 🔧 Como Adicionar Pixel Art

1. **Criar/Baixar sprites** de https://itch.io ou OpenGameArt
2. **Colocar em `/res/characters/`** (em pastas por raça)
3. **Editar `GameWindow.java`** método `createWorldPanel()`:
   ```java
   // Substituir canvas vazio por renderização de imagem
   ImageIcon spriteIcon = new ImageIcon("res/characters/elfo/idle.png");
   Image sprite = spriteIcon.getImage();
   g2d.drawImage(sprite, centerX - 16, centerY - 32, this);
   ```
4. **Compilar e testar**

## 📊 Estatísticas do Jogo

### Progressão de Level
| Level | EXP Necessária | STR | AGI | INT | HP | Mana |
|-------|---------------|-----|-----|-----|-------|------|
| 1     | 0             | 18  | 12  | 10  | 136   | 120  |
| 2     | 1000          | 20  | 13  | 12  | 140   | 124  |
| 5     | 4641          | 28  | 17  | 20  | 156   | 140  |
| 10    | 15937         | 38  | 22  | 30  | 176   | 160  |

### Inimigos (Floresta de Eldoria)
```
🐐 Goblin      - Health: 60, Dano: 15  (Nível 1-2)
🐺 Lobo        - Health: 80, Dano: 20  (Nível 2-4)
👻 Espírito    - Health: 100, Dano: 25 (Nível 4-6)
🗡️ Bandido     - Health: 120, Dano: 30 (Nível 6+)
```

## 🎬 Controles In-Game

| Botão | Ação |
|-------|------|
| ⚔️ Lutar contra Inimigo | Combate simulado |
| 😴 Descansar | Cura 50 HP |
| 🚪 Sair do Jogo | Volta ao lobby |

## 📱 Requisitos do Sistema

- **Java**: 11+ (testado com JDK 25)
- **Memória**: ~256MB RAM
- **Tela**: 1024×768 mínimo (recomendado 1920×1080)
- **Assets**: PNG com transparência recomendado

---

⭐ **Tudo pronto para receber pixel art!** Confira `/res/README_ASSETS.md` para detalhes.
