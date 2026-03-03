# 📖 Guia Completo de Desenvolvimento - Kronus Rift

> **Documento essencial para modificação e expansão do jogo.**
> Todas as configurações, parâmetros e locais de edição estão documentados aqui.

---

## 📑 Índice

1. [Arquitetura do Projeto](#arquitetura-do-projeto)
2. [Sistema de Raças](#sistema-de-raças)
3. [Sistema de Classes](#sistema-de-classes)
4. [Sistema de Habilidades Especiais](#sistema-de-habilidades-especiais)
5. [Sistema de Stats e Balanceamento](#sistema-de-stats-e-balanceamento)
6. [Sistema de Combate](#sistema-de-combate)
7. [Sistema de Experiência e Levels](#sistema-de-experiência-e-levels)
8. [Dicas de Game Design para MMORPG](#dicas-de-game-design-para-mmorpg)
9. [Guia de Persistência de Dados](#guia-de-persistência-de-dados)
10. [Troubleshooting](#troubleshooting)

---

## Arquitetura do Projeto

### Estrutura de Pastas

```
src/
├── model/
│   ├── Character.java          ⭐ STATS, STATS-UP, HABILIDADES
│   └── UserStatus.java
├── game/
│   ├── GameWorld.java          ⭐ COMBATE, DANO, INIMIGOS
│   └── GameWindow.java
├── ui/
│   ├── Xlogin.java
│   ├── Xregister.java
│   └── CppLobbyWindow.java
├── service/
│   ├── AuthenticationService.java
│   ├── RegistrationService.java
│   └── GameLauncher.java
├── util/
│   ├── CharacterManager.java   ⭐ PERSISTÊNCIA DE DADOS
│   ├── PasswordHasher.java
│   └── UIUtils.java
└── dao/
    └── UserDao.java

lib/
└── [dependências do projeto]

characters/
└── user_{userId}_characters.json  ⭐ DADOS DOS PERSONAGENS
```

### Arquivos Críticos para Modificação

| Arquivo | Função | Quando Modificar |
|---------|--------|------------------|
| `Character.java` | Model do personagem | Adicionar stats, raças, classes, habilidades |
| `GameWorld.java` | Lógica de combate | Mudar dano, inimigos, recompensas |
| `CppLobbyWindow.java` | UI do lobby | Modificar seleção de raças/classes |
| `GameWindow.java` | UI do jogo | Adicionar botões de habilidades |
| `CharacterManager.java` | Persistência JSON | Salvar novos campos de dados |

---

## Sistema de Raças

### Raças Atuais

```
1. Humano 👤     - Raça balanceada
2. Goblin 👹    - Raça rápida e ágil
3. Elfo 🧝      - Raça mágica e inteligente
```

### Como Adicionar uma Nova Raça

#### Passo 1: Adicionar à UI de Criação de Personagem

**Arquivo**: `src/ui/CppLobbyWindow.java`

**Localização**: Método `createCreationPanel()` por volta da linha ~155

```java
raceBox = new JComboBox<>(new String[]{
    "Humano 👤",
    "Goblin 👹",
    "Elfo 🧝",
    "Anão ⛏️",           // ← ADICIONE AQUI
    "Orc 🐗"             // ← E AQUI
});
```

#### Passo 2: Modificar Stats Iniciais por Raça

**Arquivo**: `src/model/Character.java`

**Localização**: Método `initializeStats()` por volta da linha ~34

```java
private void initializeStats() {
    // Determina stats baseado na RAÇA
    switch (race) {
        case "Humano":
            this.strength = 18;
            this.agility = 12;
            this.intelligence = 10;
            break;
        case "Goblin":
            this.strength = 14;      // Mais fraco
            this.agility = 18;       // MUITO ágil
            this.intelligence = 11;
            break;
        case "Elfo":
            this.strength = 16;
            this.agility = 14;
            this.intelligence = 16;  // MUITO inteligente
            break;
        case "Anão":                 // ← NOVA RAÇA
            this.strength = 22;      // MUITO forte
            this.agility = 10;       // Lento
            this.intelligence = 12;
            break;
        case "Orc":                  // ← NOVA RAÇA
            this.strength = 24;      // SUPER forte
            this.agility = 11;
            this.intelligence = 8;   // Menos inteligente
            break;
    }
    
    // Calcula saúde e mana baseado nos stats
    this.health = 100 + (strength * 2);
    this.mana = 50 + (intelligence * 2);
}
```

#### Passo 3: Adicionar Ícone na Lista do Lobby

**Arquivo**: `src/ui/CppLobbyWindow.java`

**Localização**: Método `getRaceIcon()` por volta da linha ~340

```java
private String getRaceIcon(String race) {
    return switch (race) {
        case "Humano" -> "👤";
        case "Goblin" -> "👹";
        case "Elfo" -> "🧝";
        case "Anão" -> "⛏️";    // ← ADICIONE
        case "Orc" -> "🐗";     // ← ADICIONE
        default -> "•";
    };
}
```

#### Passo 4: Atualizar JSON de Persistência

**Arquivo**: `src/util/CharacterManager.java`

> ✅ **Automaticamente compatível!** Não precisa modificar, pois salva qualquer valor de `race`.

### Dica MMORPG - Balanceamento de Raças

Em MMORPGs bem-sucedidos (WoW, FF14), as raças afetam:
- **Stats Iniciais**: Variações de 10-20% entre raças
- **Habilidades Raciais**: Bônus únicos (ex: Goblin tem 10% de velocidade extra)
- **Resgate Cultural**: Cada raça tem uma zona inicial diferente (planejado para futuro)

💡 **Sugestão**: Mantenha raças balanceadas! A diferença de força entre a raça mais forte e mais fraca não deve ser maior que 30%.

---

## Sistema de Classes

### Classes Atuais

```
1. Caçador 🏹   - Dano físico à distância (AGI-based)
2. Guerreiro ⚔️ - Dano físico corpo-a-corpo (STR-based)
3. Bruxo 🔮    - Dano mágico (INT-based)
```

### Como Adicionar uma Nova Classe

#### Passo 1: Adicionar à UI de Criação

**Arquivo**: `src/ui/CppLobbyWindow.java`

**Localização**: Método `createCreationPanel()` por volta da linha ~170

```java
classBox = new JComboBox<>(new String[]{
    "Caçador 🏹",
    "Guerreiro ⚔️",
    "Bruxo 🔮",
    "Paladino ✨",    // ← NOVA CLASSE
    "Monge 🥋"       // ← NOVA CLASSE
});
```

#### Passo 2: Modificar Bonus de Stats por Classe

**Arquivo**: `src/model/Character.java`

**Localização**: Método `initializeStats()` - adicione lógica de classe

```java
private void initializeStats() {
    // Primeiro, stats da RAÇA
    switch (race) {
        case "Humano": this.strength = 18; this.agility = 12; this.intelligence = 10; break;
        // ... outros
    }
    
    // DEPOIS, modificar por CLASSE (bônus de classe)
    switch (clazz) {
        case "Caçador":
            this.agility += 4;      // Caçadores são rápidos
            this.strength -= 2;     // Mas menos fortes
            break;
        case "Guerreiro":
            this.strength += 3;     // Guerreiros são fortes
            this.intelligence -= 2; // Menos inteligentes
            break;
        case "Bruxo":
            this.intelligence += 5; // Bruxos são muito inteligentes
            this.strength -= 4;     // MUITO fraco fisicamente
            break;
        case "Paladino":            // ← NOVA CLASSE
            this.strength += 2;     // Balanceado
            this.intelligence += 2; // Uso de magia sagrada
            break;
        case "Monge":               // ← NOVA CLASSE
            this.agility += 5;      // MUITO rápido
            this.intelligence += 1; // Sabedoria
            break;
    }
    
    // Calcula HP e Mana
    this.health = 100 + (strength * 2);
    this.mana = 50 + (intelligence * 2);
}
```

#### Passo 3: Adicionar Ícone

**Arquivo**: `src/ui/CppLobbyWindow.java`

**Localização**: Método `getClassIcon()` por volta da linha ~352

```java
private String getClassIcon(String clazz) {
    return switch (clazz) {
        case "Caçador" -> "🏹";
        case "Guerreiro" -> "⚔️";
        case "Bruxo" -> "🔮";
        case "Paladino" -> "✨";    // ← ADICIONE
        case "Monge" -> "🥋";       // ← ADICIONE
        default -> "•";
    };
}
```

### Dica MMORPG - Balanceamento de Classes

Em MMORPGs competitivos:
- **Nenhuma classe é 100% superior**: Cada uma tem pontos fortes e fracos
- **Rock-Paper-Scissors**: Guerreiro > Bruxo, Bruxo > Caçador, Caçador > Guerreiro
- **Sinergia com Raça**: Goblin Caçador é mais forte que Orc Caçador (mais bônus AGI)

💡 **Sugestão**: Sempre teste combinações de raça+classe para garantir que nenhuma seja OP.

---

## Sistema de Habilidades Especiais

### ⭐ COMO FUNCIONA ATUALMENTE

**Arquivo**: `src/model/Character.java` e `src/game/GameWorld.java`

Atualmente, o dano é calculado assim:
```
Dano = STR + (AGI / 2)
```

### ⭐ ADICIONANDO HABILIDADES ESPECIAIS

#### Passo 1: Criar Classe de Habilidades

**Arquivo**: Crie novo arquivo `src/model/Ability.java`

```java
package model;

/**
 * Representa uma habilidade especial de combate.
 */
public class Ability {
    private String name;
    private String description;
    private int manaCost;
    private double damageMultiplier;  // 1.5 = 150% de dano
    private long cooldownMs;          // Cooldown em milissegundos
    private long lastUsedTime = 0;
    
    public Ability(String name, String description, int manaCost, double damageMultiplier, long cooldownMs) {
        this.name = name;
        this.description = description;
        this.manaCost = manaCost;
        this.damageMultiplier = damageMultiplier;
        this.cooldownMs = cooldownMs;
    }
    
    public boolean canUse(int currentMana) {
        long now = System.currentTimeMillis();
        return currentMana >= manaCost && (now - lastUsedTime) >= cooldownMs;
    }
    
    public void use() {
        this.lastUsedTime = System.currentTimeMillis();
    }
    
    public int calculateDamage(int baseDamage) {
        return (int) (baseDamage * damageMultiplier);
    }
    
    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getManaCost() { return manaCost; }
    public double getDamageMultiplier() { return damageMultiplier; }
    public long getCooldownMs() { return cooldownMs; }
    public boolean isOnCooldown() {
        return (System.currentTimeMillis() - lastUsedTime) < cooldownMs;
    }
}
```

#### Passo 2: Adicionar Habilidades ao Character

**Arquivo**: `src/model/Character.java`

**Localização**: Adicione após a declaração de variáveis (por volta da linha ~20)

```java
public class Character implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String race;
    private String clazz;
    private long createdAt;
    private int level;
    private int strength;
    private int agility;
    private int intelligence;
    private long experience;
    private int health;
    private int mana;
    
    // ⭐ ADICIONE ISTO:
    private java.util.List<Ability> abilities = new java.util.ArrayList<>();
    
    // ... resto do código
}
```

#### Passo 3: Inicializar Habilidades por Classe

**Arquivo**: `src/model/Character.java`

**Localização**: Método `initializeStats()` - adicione ao final

```java
private void initializeStats() {
    // ... código anterior de race/class ...
    
    // ⭐ ADICIONE ISTO - Inicializa habilidades por classe:
    initializeAbilities();
    
    this.health = 100 + (strength * 2);
    this.mana = 50 + (intelligence * 2);
}

// ⭐ NOVO MÉTODO:
private void initializeAbilities() {
    abilities.clear();
    
    switch (clazz) {
        case "Guerreiro":
            // Golpe Poderoso: 150% de dano, custa 20 mana, cooldown 3s
            abilities.add(new Ability(
                "Golpe Poderoso",
                "Ataque devastador que causa 150% de dano",
                20, 1.5, 3000
            ));
            // Berserker: 200% de dano, custa 50 mana, cooldown 10s
            abilities.add(new Ability(
                "Fúria Berserker",
                "Ataque selvagem que causa 200% de dano",
                50, 2.0, 10000
            ));
            break;
            
        case "Caçador":
            // Tiro Preciso: 120% de dano, custa 15 mana, cooldown 2s
            abilities.add(new Ability(
                "Tiro Preciso",
                "Ataque preciso que causa 120% de dano",
                15, 1.2, 2000
            ));
            // Chuva de Flechas: 140% de dano, custa 30 mana, cooldown 5s
            abilities.add(new Ability(
                "Chuva de Flechas",
                "Múltiplos tiros causam 140% de dano",
                30, 1.4, 5000
            ));
            break;
            
        case "Bruxo":
            // Bola de Fogo: 170% de dano, custa 25 mana, cooldown 2.5s
            abilities.add(new Ability(
                "Bola de Fogo",
                "Fogo mágico que causa 170% de dano",
                25, 1.7, 2500
            ));
            // Maldição: 190% de dano, custa 40 mana, cooldown 8s
            abilities.add(new Ability(
                "Maldição Sombria",
                "Magia sombria que causa 190% de dano",
                40, 1.9, 8000
            ));
            break;
            
        case "Paladino":
            // Golpe Sagrado: 160% de dano, custa 30 mana, cooldown 4s
            abilities.add(new Ability(
                "Golpe Sagrado",
                "Ataque sagrado que causa 160% de dano",
                30, 1.6, 4000
            ));
            break;
            
        case "Monge":
            // Punho do Dragão: 180% de dano, custa 20 mana, cooldown 2.5s
            abilities.add(new Ability(
                "Punho do Dragão",
                "Técnica ancestral que causa 180% de dano",
                20, 1.8, 2500
            ));
            break;
    }
}
```

#### Passo 4: Adicionar Getters de Habilidades

**Arquivo**: `src/model/Character.java`

**Localização**: Adicione ao final dos getters (por volta da linha ~50)

```java
// ⭐ ADICIONE ISTO:
public java.util.List<Ability> getAbilities() { 
    return new java.util.ArrayList<>(abilities); 
}

public Ability getAbility(int index) {
    if (index >= 0 && index < abilities.size()) {
        return abilities.get(index);
    }
    return null;
}

public Ability useAbility(int index) {
    Ability ability = getAbility(index);
    if (ability != null && ability.canUse(this.mana)) {
        this.mana -= ability.getManaCost();
        ability.use();
        return ability;
    }
    return null;
}
```

#### Passo 5: Integrar Habilidades no Combate

**Arquivo**: `src/game/GameWorld.java`

**Localização**: Método `fightEnemy()` por volta da linha ~33

```java
public void fightEnemy(String enemyName, int abilityIndex) {
    Character player = playerCharacter;
    int baseDamage = player.getStrength() + (player.getAgility() / 2);
    
    // ⭐ ADICIONE ISTO - Usar habilidade se fornecido índice válido
    int finalDamage = baseDamage;
    if (abilityIndex >= 0) {
        Ability ability = player.useAbility(abilityIndex);
        if (ability != null) {
            finalDamage = ability.calculateDamage(baseDamage);
            System.out.println("Habilidade usada: " + ability.getName() + " (" + finalDamage + " dano)");
        } else {
            System.out.println("Não pode usar habilidade! Verificar mana e cooldown.");
        }
    }
    
    int enemyHealth = 50 + (playerCharacter.getLevel() * 10);
    
    // Combate simplificado
    while (enemyHealth > 0 && playerCharacter.getHealth() > 0) {
        // Jogador ataca
        enemyHealth -= finalDamage;  // ⭐ MUDADO: usa finalDamage
        if (enemyHealth <= 0) break;
        
        // Inimigo ataca
        int enemyDamage = 15 - (playerCharacter.getAgility() / 5);
        playerCharacter.takeDamage(enemyDamage);
    }
    
    if (playerCharacter.getHealth() > 0) {
        enemiesDefeated++;
        long expReward = 100L * playerCharacter.getLevel();
        playerCharacter.addExperience(expReward);
        playerCharacter.heal(30);
    }
}

// ⭐ ADICIONE ESTE MÉTODO TAMBÉM:
public void fightEnemy(String enemyName) {
    fightEnemy(enemyName, -1);  // Sem habilidade especial
}
```

#### Passo 6: Adicionar Botões de Habilidades na UI

**Arquivo**: `src/game/GameWindow.java`

**Localização**: Método `createActionPanel()` - adicione botões de habilidades

```java
private JPanel createActionPanel() {
    JPanel panel = new JPanel();
    panel.setBackground(new Color(30, 30, 40));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

    // ⭐ ADICIONE ISTO - Painel de Habilidades
    JPanel abilitiesPanel = new JPanel();
    abilitiesPanel.setBackground(new Color(30, 30, 40));
    abilitiesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 6));

    java.util.List<model.Ability> abilities = character.getAbilities();
    for (int i = 0; i < abilities.size(); i++) {
        model.Ability ability = abilities.get(i);
        final int abilityIndex = i;
        
        JButton abilityBtn = new JButton(ability.getName());
        abilityBtn.setFont(new Font("Arial", Font.BOLD, 10));
        abilityBtn.setBackground(new Color(100, 150, 200));
        abilityBtn.setForeground(Color.WHITE);
        abilityBtn.setFocusPainted(false);
        abilityBtn.setToolTipText(ability.getDescription() + " | Mana: " + ability.getManaCost());
        
        abilityBtn.addActionListener(e -> {
            gameWorld.fightEnemy("Inimigo Comum", abilityIndex);
            updateStatsDisplay();
            checkIfDead();
        });
        
        abilitiesPanel.add(abilityBtn);
    }

    JLabel abilitiesLabel = new JLabel("⚡ Habilidades Especiais:");
    abilitiesLabel.setFont(new Font("Arial", Font.BOLD, 12));
    abilitiesLabel.setForeground(new Color(255, 150, 50));

    panel.add(abilitiesLabel);
    panel.add(abilitiesPanel);
    panel.add(Box.createVerticalStrut(12));

    // ⭐ RESTO DOS BOTÕES NORMAIS:
    JLabel combatLabel = new JLabel("⚔️ Combate:");
    combatLabel.setFont(new Font("Arial", Font.BOLD, 12));
    combatLabel.setForeground(new Color(255, 100, 100));
    
    // ... botões normais de combate ...
    
    return panel;
}
```

### ⭐ ESTRUTURA DE BALANCEAMENTO DE HABILIDADES

```
┌─────────────────────────────────────────────────────┐
│          FÓRMULA DE DANO COM HABILIDADE              │
├─────────────────────────────────────────────────────┤
│                                                     │
│  BaseDamage = STR + (AGI / 2)                       │
│  HabilityDamage = BaseDamage × DamageMultiplier     │
│                                                     │
│  Exemplo:                                           │
│  - Guerreiro Lvl 10: STR=38, AGI=15                │
│  - BaseDamage = 38 + (15/2) = 45 dano              │
│  - Golpe Poderoso (1.5x): 45 × 1.5 = 67 dano      │
│  - Fúria Berserker (2.0x): 45 × 2.0 = 90 dano     │
│                                                     │
└─────────────────────────────────────────────────────┘
```

### 📊 Tabela de Exemplo - Habilidades por Classe

| Classe | Habilidade | Dano | Mana | Cooldown | Tipo |
|--------|-----------|------|------|----------|------|
| Guerreiro | Golpe Poderoso | 150% | 20 | 3s | Físico |
| Guerreiro | Fúria Berserker | 200% | 50 | 10s | Físico |
| Caçador | Tiro Preciso | 120% | 15 | 2s | Físico |
| Caçador | Chuva de Flechas | 140% | 30 | 5s | Físico |
| Bruxo | Bola de Fogo | 170% | 25 | 2.5s | Mágico |
| Bruxo | Maldição Sombria | 190% | 40 | 8s | Mágico |
| Paladino | Golpe Sagrado | 160% | 30 | 4s | Sagrado |
| Monge | Punho do Dragão | 180% | 20 | 2.5s | Marcial |

### 💡 Dica MMORPG - Design de Habilidades

1. **Cooldown**: Quanto maior o dano, maior o cooldown (previne spam)
2. **Mana Cost**: Habilidades super poderosas custam mais mana
3. **Balanceamento PvE vs PvP**: PvE pode ter multipliers maiores (6x+), PvP requer mais equilíbrio
4. **Efeitos Especiais**: Considerando adicionar no futuro:
   - Slow (reduz velocidade do inimigo)
   - Stun (congela inimigo)
   - DoT (dano contínuo)
   - Heal (cura aliados)

---

## Sistema de Stats e Balanceamento

### Stats Atuais

```
STR (Força):        Dano físico direto
AGI (Agilidade):    Evasão, dano à distância (AGI/2)
INT (Inteligência): Dano mágico, mana total
HP (Saúde):         Vitalidade (100 + STR×2)
Mana:               Reserva para habilidades (50 + INT×2)
```

### Como Buffar/Nerfar Stats

#### Opção 1: Modificar Stats Iniciais (Character.java)

**Arquivo**: `src/model/Character.java`

**Localização**: Método `initializeStats()` por volta da linha ~34

```java
private void initializeStats() {
    // ANTES (padrão Humano):
    // this.strength = 18;
    // DEPOIS (buffado):
    this.strength = 20;  // +2 de buff
    
    // ... outros stats ...
}
```

**Impacto**: Afeta personagens novos (Level 1)

#### Opção 2: Modificar Stats no Level-Up (Character.java)

**Arquivo**: `src/model/Character.java`

**Localização**: Método `levelUp()` por volta da linha ~65

```java
public void levelUp() {
    this.level++;
    
    // ANTES (padrão):
    // this.strength += 2;
    // DEPOIS (buffado):
    this.strength += 3;      // +1 de buff por level
    
    this.agility += 1;
    this.intelligence += 2;
    
    // Recalcula HP e Mana
    this.health = 100 + (strength * 2);
    this.mana = 50 + (intelligence * 2);
}
```

**Impacto**: Afeta todos os levels acima de 1

#### Opção 3: Modificar Fórmula de HP/Mana

**Arquivo**: `src/model/Character.java`

**Localização**: Método `initializeStats()` e `levelUp()` - última linha

```java
// ANTES:
// this.health = 100 + (strength * 2);

// DEPOIS (mais HP):
this.health = 120 + (strength * 2.5);  // +20 base e +0.5 multiplicador

// Ou:
this.health = 100 + (strength * 3);    // Simples - +1 multiplicador
```

### Tabela de Configuração - Stats por Level

**Arquivo**: `src/model/Character.java` - Método `levelUp()`

```java
public void levelUp() {
    this.level++;
    
    // Fórmula atual (linear):
    this.strength += 2;
    this.agility += 1;
    this.intelligence += 2;
    
    // ⭐ OPÇÃO: Exponencial (progressão mais rápida em níveis altos)
    // int levelBonus = level / 5 + 1;  // A cada 5 levels, +1
    // this.strength += 2 + levelBonus;
    // this.agility += 1 + (levelBonus / 2);
    // this.intelligence += 2 + levelBonus;
    
    this.health = 100 + (strength * 2);
    this.mana = 50 + (intelligence * 2);
}
```

### 📊 Tabela de Progressão Atual

| Level | STR | AGI | INT | HP | Mana |
|-------|-----|-----|-----|----|----|
| 1 (Humano) | 18 | 12 | 10 | 136 | 70 |
| 5 | 26 | 16 | 18 | 152 | 86 |
| 10 | 36 | 22 | 28 | 172 | 106 |
| 20 | 56 | 32 | 48 | 212 | 146 |

### 🎮 Dica MMORPG - Balanceamento Exponencial

Considere implementar progressão exponencial:

```
Level 1-10:   Jogadores novos, aprendem mecânicas
Level 11-30:  Gameplay midgame, estatísticas aumentam 20%
Level 31-60:  Endgame, stats aumentam 50% mais
```

---

## Sistema de Combate

### Fórmula de Dano Atual

```
DanoAtaque = STR + (AGI / 2)
VidaInimigo = 50 + (Level × 10)
DanoInimigo = 15 - (AGI / 5)
```

### Como Ajustar Dificuldade do Combate

#### Opção 1: Modificar HP do Inimigo

**Arquivo**: `src/game/GameWorld.java`

**Localização**: Método `fightEnemy()` por volta da linha ~37

```java
// ANTES:
// int enemyHealth = 50 + (playerCharacter.getLevel() * 10);

// DEPOIS (Fácil - menos HP):
int enemyHealth = 40 + (playerCharacter.getLevel() * 8);

// Ou Difícil (mais HP):
int enemyHealth = 80 + (playerCharacter.getLevel() * 15);

// Ou muito difícil (2x mais HP):
int enemyHealth = (50 + (playerCharacter.getLevel() * 10)) * 2;
```

#### Opção 2: Modificar Dano do Inimigo

**Arquivo**: `src/game/GameWorld.java`

**Localização**: Método `fightEnemy()` por volta da linha ~45

```java
// ANTES:
// int enemyDamage = baseDamage - (playerCharacter.getAgility() / 5);

// DEPOIS (Inimigo mais fraco):
int enemyDamage = 10 - (playerCharacter.getAgility() / 5);

// Ou Inimigo mais forte:
int enemyDamage = 25 - (playerCharacter.getAgility() / 5);

// Ou Inimigo muito forte (% de dano):
int enemyDamage = (int) ((baseDamage * 0.8) - (playerCharacter.getAgility() / 5));
```

#### Opção 3: Modificar Recompensa de XP

**Arquivo**: `src/game/GameWorld.java`

**Localização**: Método `fightEnemy()` por volta da linha ~51

```java
// ANTES:
// long expReward = 100L * playerCharacter.getLevel();

// DEPOIS (menos XP - progresso mais lento):
long expReward = 75L * playerCharacter.getLevel();

// Ou mais XP (progresso mais rápido):
long expReward = 150L * playerCharacter.getLevel();

// Ou progressivo (quanto mais alto o level, mais XP):
long expReward = (long) (100L * playerCharacter.getLevel() * Math.pow(1.1, playerCharacter.getLevel()));
```

### Dica MMORPG - Curva de Dificuldade

Uma boa progressão segue assim:

```
Levels 1-10:   100% de dificuldade (baseline)
Levels 11-20:  130% de dificuldade
Levels 21-30:  160% de dificuldade
Levels 30+:    200% de dificuldade (content para jogadores experientes)
```

---

## Sistema de Experiência e Levels

### Fórmula de XP Atual

```
XP Requerido para Level-Up = 1000 × (1.1)^(Level-1)
```

**Arquivo**: `src/model/Character.java`

**Localização**: Método `checkLevelUp()` por volta da linha ~60

### Como Modificar Curva de XP

#### Opção 1: Ajustar Multiplicador

```java
// ANTES:
long expRequired = (long) (1000 * Math.pow(1.1, level - 1));

// DEPOIS - Progressão mais lenta (multiplicador 1.05):
long expRequired = (long) (1000 * Math.pow(1.05, level - 1));

// Ou progressão mais rápida (multiplicador 1.2):
long expRequired = (long) (1000 * Math.pow(1.2, level - 1));

// Ou linear (mesmo XP por level):
long expRequired = 1000 * level;
```

### 📊 Tabela de XP Necessária

| Level | XP Necessário | XP Total Acumulado |
|-------|---------------|--------------------|
| 1→2 | 1000 | 1000 |
| 2→3 | 1100 | 2100 |
| 3→4 | 1210 | 3310 |
| 5→6 | 1464 | 6051 |
| 10→11 | 2594 | 20968 |

---

## Dicas de Game Design para MMORPG

### 1. **Triângulo de Balanceamento (Rock-Paper-Scissors)**

```
        BRUXO
       /     \
      /       \
  CAÇADOR --- GUERREIRO

- Guerreiro > Bruxo (físico vence mágico)
- Bruxo > Caçador (mágico vence distância)
- Caçador > Guerreiro (distância vence corpo-a-corpo)
```

### 2. **Curva de Progresso Ideal**

```
Level 1-5:    Tutorial/Aprendizado (progressão rápida)
Level 6-15:   Gameplay principal (progressão normal)
Level 16-30:  Conteúdo intermediário (progressão desafiadora)
Level 30+:    Endgame (progressão muito lenta)
```

### 3. **Economia de Recursos (Mana)**

- **Habilidades fracas**: 10-20 mana
- **Habilidades médias**: 25-40 mana
- **Habilidades fortes**: 50+ mana
- **Mana regen**: (INT / 10) por segundo seria ideal

### 4. **Sistem de Cooldown**

```
Cooldown Recomendado por Tipo:

Habilidades 1-1.3x dano:  1-2 segundos
Habilidades 1.4-1.7x dano: 2-5 segundos
Habilidades 1.8-2.0x dano: 5-10 segundos
Habilidades 2.0+ dano:    10+ segundos
```

### 5. **Prevent Player Frustration**

⚠️ **NÃO FAÇA**:
- Chance de miss (jogador odeia aleatoriedade)
- Dano aleatório (mantém mesma fórmula sempre)
- Inimigos um-shotando (sem aviso)

✅ **FAÇA**:
- Dano consistente e previsível
- Dificuldade progressiva clara
- Boss fights com padrões (ataque → pausa)

### 6. **Engagement Loop**

```
Mata inimigo → Ganha XP → Level-Up → Stats aumentam → Mata inimigos mais fortes
↑____________________________________________________________________________↓
```

---

## Guia de Persistência de Dados

### Estrutura JSON Atual

**Arquivo**: `characters/user_{userId}_characters.json`

```json
[
  {
    "name": "Arkanos",
    "race": "Elfo",
    "clazz": "Bruxo",
    "level": 6,
    "strength": 28,
    "agility": 17,
    "intelligence": 20,
    "health": 156,
    "mana": 90,
    "experience": 18000,
    "createdAt": 1772566942669
  }
]
```

### Como Adicionar Novos Campos de Dados

#### Passo 1: Adicionar Campo na Classe Character

**Arquivo**: `src/model/Character.java`

```java
public class Character implements Serializable {
    // ... campos existentes ...
    
    // ⭐ ADICIONE UM NOVO CAMPO:
    private int armorRating = 0;      // Novo: Armadura
    private int weaponDamage = 0;     // Novo: Dano de arma
    
    // Getter/Setter:
    public int getArmorRating() { return armorRating; }
    public void setArmorRating(int armor) { this.armorRating = armor; }
    
    public int getWeaponDamage() { return weaponDamage; }
    public void setWeaponDamage(int damage) { this.weaponDamage = damage; }
}
```

#### Passo 2: Salvar Campo no JSON

**Arquivo**: `src/util/CharacterManager.java`

**Localização**: Método `saveCharacters()` por volta da linha ~27

```java
// Adicione a linha de novo campo:
json.append("    \"armorRating\": ").append(ch.getArmorRating()).append(",\n");
json.append("    \"weaponDamage\": ").append(ch.getWeaponDamage()).append(",\n");
```

#### Passo 3: Carregar Campo do JSON

**Arquivo**: `src/util/CharacterManager.java`

**Localização**: Método `loadCharacters()` por volta da linha ~80

```java
// Extrair novo campo:
String armorStr = extractJsonNumberValue(block, "armorRating");
String weaponStr = extractJsonNumberValue(block, "weaponDamage");

// Aplicar ao character:
if (!armorStr.isEmpty()) {
    try {
        character.setArmorRating(Integer.parseInt(armorStr));
        character.setWeaponDamage(Integer.parseInt(weaponStr));
    } catch (NumberFormatException e) {
        // Valor padrão se houver erro
    }
}
```

#### Passo 4: Atualizar Método `restoreStats()`

**Arquivo**: `src/model/Character.java`

```java
// Adicione parâmetros ao método:
public void restoreStats(
    int level, int strength, int agility, int intelligence, 
    int health, int mana, long experience,
    int armorRating,      // ⭐ NOVO
    int weaponDamage      // ⭐ NOVO
) {
    this.level = level;
    this.strength = strength;
    this.agility = agility;
    this.intelligence = intelligence;
    this.health = health;
    this.mana = mana;
    this.experience = experience;
    this.armorRating = armorRating;      // ⭐ NOVO
    this.weaponDamage = weaponDamage;    // ⭐ NOVO
}
```

---

## Troubleshooting

### Problema: Personagem Level não atualiza no Lobby

**Causa**: `CppLobbyWindow` não está recarregando dados ao ficar visível

**Solução**:
```java
// Arquivo: src/ui/CppLobbyWindow.java
// Verifique se tem listeners:
addWindowListener(new WindowAdapter() {
    @Override
    public void windowActivated(WindowEvent e) {
        characters = CharacterManager.loadCharacters(userId);
        refreshCharacterList();
    }
});
```

### Problema: JSON corrompido / não salva

**Causa**: Caracteres especiais ou campos não formatados

**Solução**:
1. Deletar arquivo: `characters/user_{userId}_characters.json`
2. Recriar personagem
3. Verificar `CharacterManager.java` - method `saveCharacters()` está correto

### Problema: Habilidade não aparece na UI

**Causa**: `GameWindow.java` não tem os botões de habilidades

**Solução**:
1. Verificar método `createActionPanel()`
2. Adicionar loop de habilidades
3. Compilar e testar

### Problema: Dano muito alto/baixo

**Causa**: Fórmula de dano desbalanceada

**Solução**: Ajuste em `GameWorld.java`:
```java
// Defina baseDamage explicitamente:
int baseDamage = playerCharacter.getStrength() + (playerCharacter.getAgility() / 2);
System.out.println("Dano calculado: " + baseDamage);  // Debug
```

---

## Checklist de Modificações Comum

### ✅ Quando Adicionar Nova Raça:
- [ ] Adicionar à UI (CppLobbyWindow.java - raceBox)
- [ ] Adicionar stats (Character.java - initializeStats())
- [ ] Adicionar ícone (CppLobbyWindow.java - getRaceIcon())

### ✅ Quando Adicionar Nova Classe:
- [ ] Adicionar à UI (CppLobbyWindow.java - classBox)
- [ ] Adicionar stats (Character.java - initializeStats())
- [ ] Adicionar habilidades (Character.java - initializeAbilities())
- [ ] Adicionar ícone (CppLobbyWindow.java - getClassIcon())
- [ ] Adicionar botões na UI (GameWindow.java - createActionPanel())

### ✅ Quando Buffar/Nerfar:
- [ ] Modificar stats iniciais (Character.java - initializeStats())
- [ ] Ou modificar level-up (Character.java - levelUp())
- [ ] Testar com múltiplos personagens
- [ ] Verificar balanceamento classe vs classe

### ✅ Quando Mudar Dificuldade:
- [ ] Ajustar HP inimigo (GameWorld.java - fightEnemy())
- [ ] Ajustar dano inimigo (GameWorld.java - fightEnemy())
- [ ] Ajustar XP reward (GameWorld.java - fightEnemy())
- [ ] Testar progresso em múltiplos níveis

---

## Referência Rápida - Arquivos Críticos

```
┌─────────────────────────────────────────────────────────┐
│               MODIFICAÇÕES MAIS COMUNS                   │
├─────────────────────────────────────────────────────────┤
│                                                         │
│ 🔷 Character.java                                       │
│    ├─ Adicionar raças: initializeStats() switch race   │
│    ├─ Adicionar classes: initializeStats() switch class│
│    └─ Adicionar habilidades: initializeAbilities()    │
│                                                         │
│ 🔶 GameWorld.java                                       │
│    ├─ Mudar dificuldade: fightEnemy() enemyHealth      │
│    ├─ Mudar XP: expReward calculation                  │
│    └─ Mudar dano inimigo: enemyDamage calculation      │
│                                                         │
│ 🟡 CppLobbyWindow.java                                 │
│    ├─ Adicionar raças UI: raceBox JComboBox           │
│    ├─ Adicionar classes UI: classBox JComboBox        │
│    └─ Adicionar ícones: getRaceIcon()/getClassIcon()  │
│                                                         │
│ 🟢 GameWindow.java                                      │
│    ├─ Adicionar habilidades UI: createActionPanel()   │
│    └─ Mostrar stats: updateStatsDisplay()             │
│                                                         │
│ 🟠 CharacterManager.java                               │
│    ├─ Adicionar campos JSON: saveCharacters()          │
│    └─ Carregar campos JSON: loadCharacters()           │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

## Template de Mudança Rápida

Use este template quando estiver fazendo mudanças:

```markdown
## 🔧 Mudança: [Nome da mudança]

**Objetivo**: [O que você quer fazer]

**Arquivos Afetados**:
- [ ] Character.java
- [ ] GameWorld.java
- [ ] CppLobbyWindow.java
- [ ] GameWindow.java
- [ ] CharacterManager.java

**Pasos**:
1. [Passo 1]
2. [Passo 2]
3. [Passo 3]
4. Compilar: `javac -cp "lib/*" -d out src/**/*.java`
5. Testar

**Balanceamento**:
- [ ] Verificar stats balanceados
- [ ] Verificar XP apropriado
- [ ] Verificar dificuldade

**Resultado**: ✅ Completo
```

---

## Contato & Suporte

Se encontrar bugs ou desequilíbrios:
1. Verifique este guia primeiro
2. Teste com múltiplos personagens
3. Compare com tabelas de referência
4. Adjust parâmetros incrementalmente (10% por vez)

---

**Última atualização**: 03/03/2026  
**Versão**: 1.0  
**Compatibilidade**: JDK 25+  
**Framework**: Swing + SQLite

Bom desenvolvimento! 🚀
