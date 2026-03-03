# 🎮 Sistema de Mundo & Level do Kronus Rift

## ✅ O que foi implementado

### 1. **Sistema de Stats Completo** (Character.java)
```
📊 Estatísticas Base:
├─ Level: 1 (aumenta a cada levelup)
├─ ❤️ Health: 100 + (STR × 2)
├─ 💙 Mana: 50 + (INT × 2)
├─ ⚔️ STR (Força): 18 [melhora em +2 por level]
├─ 🏃 AGI (Agilidade): 12 [melhora em +1 por level]
└─ 🧠 INT (Inteligência): 10 [melhora em +2 por level]
```

### 2. **Mundo Inicial - Floresta de Eldoria** (GameWorld.java)
- Primeiro mundo onde todos os personagens iniciam
- Sistema de combate simplificado
- Dano baseado em stats (STR + AGI/2)
- Recompensa de experiência por combate
- Cura automática após vitória

### 3. **Interface do Jogo Visual** (GameWindow.java)
```
┌─────────────────────────────────────────────────────┐
│ 📍 KRONUS RIFT - Floresta de Eldoria                │
├──────────────────────┬──────────────────────────────┤
│                      │ 📊 Estatísticas              │
│   [MAPA 2D/PIXEL]    │ Level: 1                     │
│   (espaço para       │ ❤️ HP: 100/136              │
│    pixel art)        │ 💙 Mana: 100/120            │
│                      │                              │
│   [Sprite do         │ ⚔️ STR: 18                  │
│    personagem]       │ 🏃 AGI: 12                  │
│                      │ 🧠 INT: 10                  │
│                      │                              │
│   [Grid 32x32]       │ 👹 Inimigos: 0              │
│                      │                              │
│   Log do Jogo:       │ [⚔️ Lutar]                  │
│   > Entraste...      │ [😴 Descansar]              │
│   > Derrotaste...    │ [Sair do Jogo]              │
│                      │                              │
└──────────────────────┴──────────────────────────────┘
│ ⏱️ Duração: 00:05:23                                 │
└──────────────────────────────────────────────────────┘
```

### 4. **Integração no Lobby** (CppLobbyWindow.java)
- Botão "Entrar" abre `GameWindow` para o personagem selecionado
- Transição suave do lobby → mundo do jogo
- Personagem mantém os stats salvos

## 🎨 Espaço para Pixel Art

### Locais onde você pode adicionar assets:
```
/res/
├── characters/
│   ├── humano/      ← Sprites do personagem (32x48px)
│   ├── goblin/
│   └── elfo/
├── enemies/         ← Sprites dos inimigos
│   ├── goblin_enemy.png
│   ├── wolf.png
│   ├── forest_spirit.png
│   └── bandit.png
└── environments/    ← Background e tileset
    ├── forest_bg.png (parallax background)
    └── grass_tileset.png (32x32 tiles)
```

### Como o código está preparado:
- `GameWindow.createWorldPanel()` → Canvas vazio esperando pixel art
- Grid de 32x32 como guia para tilesets
- Espaço central para renderizar sprite do personagem
- Placeholder com instruções para facilitar integração

## 📈 Sistema de Level-Up

```
Experiência por Combate: 100 XP × Level Atual
EXP para Next Level: 1000 × 1.1^(level-1)

Exemplo:
Level 1 → 2: 1000 XP
Level 2 → 3: 1100 XP
Level 3 → 4: 1210 XP
...

Por cada Level-Up:
├─ STR +2
├─ AGI +1
├─ INT +2
├─ HP regenera (baseado em STR)
└─ Mana regenera (baseado em INT)
```

## 🔄 Fluxo de Jogo Completo

```
1. Login (Xlogin)
   ↓
2. Lobby (CppLobbyWindow)
   ├─ Criar novo personagem → Stats padrão
   └─ Selecionar personagem → Botão "Entrar"
   ↓
3. Mundo - Floresta de Eldoria (GameWindow)
   ├─ Visualizar stats em tempo real
   ├─ Lutar contra inimigos
   │  ├─ Ganhar XP
   │  ├─ Fazer level-up automático
   │  └─ Receber cura
   ├─ Descansar para curar
   └─ Sair (volta ao lobby)
```

## 💾 Persistência de Dados

### Personagem (JSON local):
```json
{
  "name": "Aragorn",
  "race": "Elfo",
  "clazz": "Guerreiro",
  "level": 1,
  "strength": 18,
  "agility": 12,
  "intelligence": 10,
  "health": 136,
  "mana": 120,
  "experience": 0,
  "createdAt": 1741027843500
}
```

## 🚀 Como Testar Agora

```powershell
cd D:\xlogin-project-java\Project-xlogin-java

# Compilar (já foi feito, mas se precisar regenerar)
javac -cp "lib/*" -d out src\model\Character.java src\game\*.java src\ui\CppLobbyWindow.java src\util\CharacterManager.java

# Executar
java -cp "out;lib/*" ui.Xlogin
```

**Fluxo de Teste:**
1. Login com usuário válido
2. Criar personagem (ex: "Aragorn", Elfo, Guerreiro)
3. Clicar "Entrar"
4. Interface do jogo abre
5. Clicar "Lutar contra Inimigo"
6. Stats atualizam em tempo real
7. Ganhe XP e faça level-up
8. Sair do jogo volta ao lobby

## 📝 Próximas Melhorias

- [ ] Integrar sprites pixel art em `/res/`
- [ ] Animar sprites (idle, walk, attack)
- [ ] Adicionar mais inimigos e dungeons
- [ ] Sistema de inventário
- [ ] Quests e NPCs
- [ ] Multiplayer (PvP/cooperativo)
- [ ] Banco de dados para persistência (em vez de JSON)

---

**Status**: ✅ Funcional e pronto para pixel art!
