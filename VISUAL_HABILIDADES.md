# 🌟 GUIA VISUAL - Sistema de Habilidades Especiais

## Arquitetura do Sistema

```
┌──────────────────────────────────────────────────────────────────┐
│                   PERSONAGEM (Character.java)                    │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Stats:                          Habilidades:                   │
│  ├─ STR (Força)      18         ├─ Golpe Poderoso              │
│  ├─ AGI (Agilidade)  12         │  └─ 150% dano, 20 mana, 3s  │
│  ├─ INT (Intel)      10         ├─ Fúria Berserker            │
│  ├─ HP               136         │  └─ 200% dano, 50 mana, 10s │
│  └─ Mana             70          └─ [Mais habilidades...]      │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

## Fluxo de Combate com Habilidades

```
JOGADOR                                    INIMIGO
  │                                          │
  ├─ Clica "Golpe Poderoso" ─────────────┐  │
  │                                      ↓  │
  │    ┌────────────────────────────────────┴──┐
  │    │ 1. Verificar Mana (20 mana)          │
  │    │ 2. Verificar Cooldown (0s elapsed)   │
  │    │ 3. Se OK: Usar Habilidade            │
  │    └────────────────────────────────────┬──┘
  │                                      ↓
  │ Calcular Dano:
  │   BaseDamage = STR + (AGI / 2)
  │   BaseDamage = 18 + (12 / 2) = 24
  │   
  │   HabilityDamage = BaseDamage × 1.5
  │   HabilityDamage = 24 × 1.5 = 36 dano
  │
  ├─ Inimigo recebe 36 dano ──────────────────→ Inimigo HP - 36
  │
  ├─ Inimigo contra-ataca ←─────────────────── -15 do jogador HP
  │
  └─ Verificar Vitória / Derrota
```

## Estrutura de Uma Habilidade

```
new Ability(
    "Nome",           ← String: Identificador visual
    "Descrição",      ← String: Tooltip no botão
    manaCost,         ← int: Custo de recurso (20-80)
    multiplier,       ← double: 1.5 = 150% de dano base
    cooldownMs        ← long: Tempo entre usos (2000-15000 ms)
)

Exemplo - Golpe Poderoso (Guerreiro):
new Ability(
    "Golpe Poderoso",
    "Ataque devastador que causa 150% de dano",
    20,      // Custo moderado
    1.5,     // 50% mais dano
    3000     // 3 segundos entre usos
)
```

## Tabela de Balanceamento Recomendado

```
┌─────────────────────────────────────────────────────────────┐
│ DANO          MANA    COOLDOWN    SITUAÇÃO                 │
├─────────────────────────────────────────────────────────────┤
│ 1.0 - 1.3x    10-20   1-2s        Habilidades baixas       │
│ 1.4 - 1.7x    20-35   2-5s        Habilidades médias       │
│ 1.8 - 2.2x    40-60   5-10s       Habilidades altas        │
│ 2.3 - 3.0x    70+     10-20s      Habilidades ultimate     │
└─────────────────────────────────────────────────────────────┘

REGRA DE OURO:
Quanto maior o dano → Maior custo de mana → Maior cooldown
(Previne spam de habilidades poderosas)
```

## Exemplo Real: Guerreiro vs Bruxo

```
GUERREIRO (STR: 20, AGI: 10)          BRUXO (STR: 10, AGI: 12, INT: 18)
│                                       │
│ BaseDamage = 20 + (10/2) = 25        │ BaseDamage = 10 + (12/2) = 16
│                                       │
├─ Usa "Golpe Poderoso" (1.5x)        ├─ Usa "Bola de Fogo" (1.7x)
│  25 × 1.5 = 37 dano                 │  16 × 1.7 = 27 dano
│                                       │
├─ Usa "Fúria Berserker" (2.0x)       ├─ Usa "Maldição Sombria" (1.9x)
│  25 × 2.0 = 50 dano ← PODEROSO!     │  16 × 1.9 = 30 dano
│                                       │
└─ Vantagem: Dano físico              └─ Vantagem: Mais habilidades
```

## UI - Painel de Habilidades na Tela

```
┌────────────────────────────────────────────────────┐
│                 ⚡ HABILIDADES ESPECIAIS            │
├────────────────────────────────────────────────────┤
│                                                    │
│  ┌──────────────────┐  ┌──────────────────┐       │
│  │ Golpe Poderoso   │  │ Fúria Berserker  │       │
│  │ (Mana: 20/70)    │  │ (Mana: 50/70)    │       │
│  │ (Dano: 150%)     │  │ (Dano: 200%)     │       │
│  │ (CD: Ready)      │  │ (CD: 5.2s)       │       │
│  └──────────────────┘  └──────────────────┘       │
│                                                    │
└────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────┐
│            ⚔️ COMBATE                              │
├────────────────────────────────────────────────────┤
│                                                    │
│  [Lutar contra Inimigo]                          │
│  [Descansar (Curar)]                             │
│  [Sair do Jogo]                                  │
│                                                    │
└────────────────────────────────────────────────────┘
```

## Fluxograma de Decisão - Usar Habilidade?

```
         ┌─────────────────────────┐
         │  Clicou em Habilidade?  │
         └────────────┬────────────┘
                      │
                      ↓
              ┌───────────────┐
              │ Mana >= Custo?│
              └───┬───────┬───┘
              SIM │       │ NÃO
                  ↓       ↓
            [USAR]     [ERRO: Mana insuficiente]
              │
              ↓
         ┌──────────────────┐
         │ Cooldown Expirado?│
         └───┬───────┬──────┘
         SIM │       │ NÃO
             ↓       ↓
          [USAR]   [ERRO: Cooldown ativo]
             │
             ↓
        ┌────────────────────────┐
        │ 1. Retirar Mana        │
        │ 2. Calcular Dano       │
        │ 3. Aplicar Cooldown    │
        │ 4. Atacar Inimigo      │
        └────────────┬───────────┘
                     │
                     ↓
            ┌────────────────┐
            │ Inimigo Morreu?│
            └───┬────────┬───┘
            SIM │        │ NÃO
                ↓        ↓
            [VITÓRIA]  [Inimigo contra-ataca]
                        │
                        ↓
                  ┌──────────────┐
                  │ Você Morreu? │
                  └───┬──────┬───┘
                  SIM │      │ NÃO
                      ↓      ↓
                 [DERROTA] [Continuar]
```

## Mapa de Cores - Tipos de Habilidades

```
┌──────────────┬──────────┬───────────────────────┐
│ Tipo         │ Cor      │ Exemplo               │
├──────────────┼──────────┼───────────────────────┤
│ Físico       │ 🔴 Vermelho | Golpe Poderoso    │
│ Mágico       │ 🔵 Azul     | Bola de Fogo      │
│ Distância    │ 🟢 Verde    | Tiro Preciso      │
│ Sagrado      │ 🟡 Amarelo  | (Futuro)          │
│ Escuro       │ 🟣 Roxo     | Maldição Sombria  │
└──────────────┴──────────┴───────────────────────┘
```

## Progressão de Dano por Nível

```
Guerreiro Lvl 1:
  STR=18, AGI=12
  Golpe Poderoso: 24 dano (18 + 6) × 1.5 = 36

Guerreiro Lvl 5:
  STR=28, AGI=16 (+2 STR, +1 AGI por level)
  Golpe Poderoso: 36 dano (28 + 8) × 1.5 = 54

Guerreiro Lvl 10:
  STR=38, AGI=22
  Golpe Poderoso: 45 dano (38 + 11) × 1.5 = 73

┌──────────┬────────┬────────────┬──────────────────┐
│ Level    │ STR    │ Dano Base  │ Golpe Poderoso   │
├──────────┼────────┼────────────┼──────────────────┤
│ 1        │ 18     │ 24         │ 36 (1.5x)        │
│ 5        │ 28     │ 36         │ 54               │
│ 10       │ 38     │ 45         │ 73               │
│ 20       │ 58     │ 63         │ 104              │
│ 30       │ 78     │ 81         │ 135              │
└──────────┴────────┴────────────┴──────────────────┘
```

## Classe: Anatomy of Ability.java

```
public class Ability {
    
    private String name;              // "Golpe Poderoso"
    private String description;       // "Ataque devastador..."
    private int manaCost;            // 20 (quanto custa usar)
    private double damageMultiplier;  // 1.5 (150% de dano)
    private long cooldownMs;         // 3000 (3 segundos)
    private long lastUsedTime;       // Timestamp do último uso
    
    // MÉTODOS PRINCIPAIS:
    
    public boolean canUse(int currentMana) {
        // Verifica se pode usar (mana + cooldown)
        return currentMana >= manaCost && !isOnCooldown();
    }
    
    public void use() {
        // Marca como usado (inicia cooldown)
        this.lastUsedTime = System.currentTimeMillis();
    }
    
    public int calculateDamage(int baseDamage) {
        // Calcula dano final
        return (int) (baseDamage * damageMultiplier);
    }
    
    public boolean isOnCooldown() {
        // Verifica se ainda está em cooldown
        long elapsed = System.currentTimeMillis() - lastUsedTime;
        return elapsed < cooldownMs;
    }
}
```

## Onde Encontrar e Modificar

```
1. ADICIONAR HABILIDADES:
   └─ src/model/Character.java
      └─ initializeAbilities() método

2. CRIAR HABILIDADES:
   └─ src/model/Ability.java
      └─ new Ability(...) construtor

3. USAR HABILIDADES:
   └─ src/game/GameWorld.java
      └─ fightEnemy(enemy, abilityIndex)

4. MOSTRAR NA UI:
   └─ src/game/GameWindow.java
      └─ createActionPanel() método

5. SALVAR EM JSON:
   └─ src/util/CharacterManager.java
      └─ saveCharacters() / loadCharacters()
```

---

## 🎓 Quiz - Teste Seu Conhecimento

**Q1**: Qual é a fórmula de dano para "Golpe Poderoso" (1.5x) com STR=20, AGI=12?
```
Resposta: 
BaseDamage = 20 + (12/2) = 26
Final = 26 × 1.5 = 39 dano
```

**Q2**: Se uma habilidade custa 50 mana e cooldown 10s, qual é ideal para...?
```
Resposta: Ultimate/finalizador (última habilidade em luta)
```

**Q3**: Como adicionar "Ataque Espiritual" com 180% dano, 30 mana, 5s cooldown?
```
Resposta:
new Ability(
    "Ataque Espiritual",
    "Descrição aqui",
    30,     // Mana
    1.8,    // 180% dano
    5000    // 5 segundos
)
```

---

**Última Atualização**: 03/03/2026  
**Versão**: 1.0

Bom desenvolvimento! 🎮✨
