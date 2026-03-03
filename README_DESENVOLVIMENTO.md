# 🎮 Kronus Rift - Developer Guide

> **Bem-vindo ao guia completo de desenvolvimento do Kronus Rift!**
>
> Este projeto é um MMORPG em desenvolvimento com sistema de personagens, combate, e habilidades especiais.

---

## 📚 Documentação Disponível

### 1. **GUIA_DESENVOLVIMENTO.md** ⭐ COMECE AQUI

O guia completo de desenvolvimento contém:

- ✅ **Arquitetura do Projeto** - Entenda a estrutura
- ✅ **Sistema de Raças** - Como adicionar novas raças (Anão, Orc, etc)
- ✅ **Sistema de Classes** - Como adicionar novas classes (Paladino, Monge, etc)
- ✅ **Sistema de Habilidades Especiais** - NOVO! Como criar habilidades com dano customizado
- ✅ **Balanceamento de Stats** - Como buffar/nerfar personagens
- ✅ **Sistema de Combate** - Como ajustar dificuldade
- ✅ **Dicas MMORPG** - Aprenha com jogos reais como WoW e FF14
- ✅ **Troubleshooting** - Solução de problemas comuns

**Como usar**:
1. Abra `GUIA_DESENVOLVIMENTO.md`
2. Procure a seção que deseja modificar
3. Siga os exemplos de código
4. Recompile com: `javac -cp "lib/*" -d out src/**/*.java`

---

## 🎯 Guia Rápido - Modificações Comuns

### Adicionar Nova Raça (5 minutos)

```bash
1. Abra: src/ui/CppLobbyWindow.java
2. Encontre: raceBox = new JComboBox<>(...)
3. Adicione: "Anão ⛏️"
4. Abra: src/model/Character.java
5. Encontre: initializeStats()
6. Adicione case para "Anão":
   case "Anão": 
       this.strength = 22;  // MUITO forte
       this.agility = 10;
       this.intelligence = 12;
       break;
7. Compile!
```

### Adicionar Nova Classe (10 minutos)

```bash
1. Siga os mesmos passos acima
2. Adicione case na classe para modificar stats
3. Adicione case em initializeAbilities() para habilidades
4. Recompile!
```

### Usar Habilidade Especial (automático)

```bash
1. Criar personagem
2. Entrar no jogo
3. Ver botões de habilidades (⚡ seção)
4. Clicar em "Golpe Poderoso" ou "Bola de Fogo"
5. Ver dano aumentado!
```

### Buffar um Personagem (3 minutos)

```bash
Em src/model/Character.java, método levelUp():

// ANTES:
this.strength += 2;

// DEPOIS (buffado):
this.strength += 3;  // +1 de buff por level
```

### Nerfar Dificuldade (2 minutos)

```bash
Em src/game/GameWorld.java, método fightEnemy():

// ANTES:
int enemyHealth = 50 + (playerCharacter.getLevel() * 10);

// DEPOIS (fácil):
int enemyHealth = 40 + (playerCharacter.getLevel() * 8);
```

---

## 🎮 Novas Habilidades Especiais - Como Adicionar

### Exemplo: Adicionar "Explosão Cósmica" para o Bruxo

**Arquivo**: `src/model/Character.java`

**Localização**: Método `initializeAbilities()` na seção "Bruxo"

```java
case "Bruxo":
    abilities.add(new Ability(
        "Bola de Fogo",
        "Fogo mágico que causa 170% de dano",
        25, 1.7, 2500
    ));
    abilities.add(new Ability(
        "Maldição Sombria",
        "Magia sombria que causa 190% de dano",
        40, 1.9, 8000
    ));
    // ⭐ ADICIONE AQUI:
    abilities.add(new Ability(
        "Explosão Cósmica",
        "Explosão devastadora do cosmos - 250% de dano!",
        80,   // Custa muito mana
        2.5,  // 250% de dano
        15000 // 15 segundos de cooldown
    ));
    break;
```

**Parâmetros da Classe Ability**:

```java
new Ability(
    "Nome da Habilidade",
    "Descrição detalhada",
    manaCost,       // Quanto de mana custa (int)
    damageMultiplier, // Multiplicador de dano (double): 1.5 = 150%
    cooldownMs      // Cooldown em milissegundos (long): 3000 = 3 segundos
)
```

**Fórmula de Dano**:
```
BaseDamage = STR + (AGI / 2)
HabilityDamage = BaseDamage × damageMultiplier

Exemplo com Bruxo Lvl 10 (INT-based):
- Força base: 10 + (3 / 2) = 11.5
- Bola de Fogo (1.7x): 11.5 × 1.7 = 19 dano
- Explosão (2.5x): 11.5 × 2.5 = 28 dano
```

---

## 📊 Tabela de Habilidades Existentes

| Classe | Habilidade | Dano | Mana | Cooldown | Tipo |
|--------|-----------|------|------|----------|------|
| **Guerreiro** | Golpe Poderoso | 150% | 20 | 3s | Físico |
| **Guerreiro** | Fúria Berserker | 200% | 50 | 10s | Físico |
| **Caçador** | Tiro Preciso | 120% | 15 | 2s | Físico |
| **Caçador** | Chuva de Flechas | 140% | 30 | 5s | Físico |
| **Bruxo** | Bola de Fogo | 170% | 25 | 2.5s | Mágico |
| **Bruxo** | Maldição Sombria | 190% | 40 | 8s | Mágico |

---

## 🛠️ Stack Tecnológico

```
Backend:
├── Java 25 (JDK-25)
├── Swing (UI Desktop)
├── SQLite (Autenticação)
└── JSON Manual (Persistência de Personagens)

Frontend:
├── Janelas Swing (JFrame, JPanel, JButton)
├── Pixel Art (res/sombra.gif - customizável)
└── Tema Dark Mode

Interoperabilidade:
└── C++ (lobby.exe - backend de seleção de personagens)
```

---

## 📁 Estrutura de Diretórios

```
Project-xlogin-java/
├── src/
│   ├── model/
│   │   ├── Character.java          ← Personagens + Habilidades
│   │   ├── Ability.java            ← NOVO: Habilidades Especiais
│   │   └── UserStatus.java
│   ├── game/
│   │   ├── GameWorld.java          ← Combate
│   │   └── GameWindow.java         ← UI do Jogo
│   ├── ui/
│   │   ├── Xlogin.java
│   │   ├── Xregister.java
│   │   └── CppLobbyWindow.java
│   ├── util/
│   │   ├── CharacterManager.java   ← Salva/Carrega JSON
│   │   └── PasswordHasher.java
│   └── dao/
│       └── UserDao.java
├── GUIA_DESENVOLVIMENTO.md         ← 📖 GUIA COMPLETO
├── README.md                       ← Este arquivo
└── lib/
    └── [dependências]
```

---

## 🚀 Como Compilar e Executar

### Compilar

```bash
cd Project-xlogin-java
javac -cp "lib/*" -d out src/model/*.java src/util/*.java src/dao/*.java src/service/*.java src/game/*.java src/ui/*.java
```

### Executar

```bash
java -cp "lib/*;out" ui.Xlogin
```

### Credenciais de Teste

```
Usuário: teste123
Senha: teste123
```

---

## 🎓 Aprendendo com MMORPG Reais

### World of Warcraft (WoW) - Inspiração

```
✅ Cada raça tem stats iniciais diferentes
✅ Cada classe tem habilidades únicas
✅ Cooldowns em habilidades poderosas previne spam
✅ Progressão exponencial (quanto mais alto, mais xp needed)
✅ Balanceamento Rock-Paper-Scissors entre classes
```

### Final Fantasy XIV - Inspiração

```
✅ Todos podem aprender todas as classes
✅ Habilidades têm "Global Cooldown" (GCD)
✅ Efeitos especiais (DoT, HoT, Buff)
✅ Sinergias entre classes (suporte mútuo)
```

### Diablo - Inspiração

```
✅ Dano aumenta exponencialmente com level
✅ Habilidades têm efeitos secundários (stun, slow, etc)
✅ Itens equipáveis modificam stats
✅ Progressão rápida no início, lenta no endgame
```

---

## 🔧 Troubleshooting

### Problema: "ClassNotFoundException"

```
java -cp "lib/*;out" ui.Xlogin
                      ↑ Precisa de ";" em Windows
```

### Problema: Habilidades não aparecem

```
1. Verifique: src/game/GameWindow.java
2. Procure: createActionPanel()
3. Verifique if (abilities.size() > 0)
4. Recompile!
```

### Problema: Dano muito alto/baixo

```
Arquivo: src/game/GameWorld.java
Método: fightEnemy()
Variável: baseDamage = playerCharacter.getStrength() + (playerCharacter.getAgility() / 2)

Debug: Adicione print:
System.out.println("Dano calculado: " + baseDamage);
```

### Problema: JSON corrompido

```
1. Delete: characters/user_*.json
2. Recriar personagem
3. Teste novamente
```

---

## 📋 Checklist - Antes de Fazer Push

- [ ] Recompilou o código: `javac -cp "lib/*" -d out src/**/*.java`
- [ ] Testou o jogo (criar char → level up → salvar)
- [ ] Verificou balanceamento (não muito OP)
- [ ] Adicionou comentários no código
- [ ] Atualizou este README.md se necessário

---

## 🎨 Customizações Futuras

```
[ ] Sistema de Inventário
[ ] Equipamentos (armadura, armas)
[ ] Efeitos Especiais (DoT, Stun, Slow)
[ ] Múltiplos Mundos/Dungeons
[ ] PvP Arena
[ ] Boss Fights
[ ] Quest System
[ ] Guild System
[ ] Trading
[ ] Mount System
[ ] Profissões (crafting, mining)
```

---

## 📞 Contato & Suporte

Se encontrar bugs ou tiver dúvidas:

1. **Verifique este README.md** - Soluções rápidas
2. **Abra GUIA_DESENVOLVIMENTO.md** - Documentação completa
3. **Teste com print statements** - Debug básico
4. **Recompile limpo** - Delete `out/` e recompile

---

## 📜 Changelog

### v1.0 (03/03/2026)

- ✅ Sistema de Raças (Humano, Goblin, Elfo)
- ✅ Sistema de Classes (Guerreiro, Caçador, Bruxo)
- ✅ Sistema de Combate baseado em Stats
- ✅ Sistema de Experiência e Level-Up
- ✅ **NOVO**: Sistema de Habilidades Especiais com Dano Customizado
- ✅ Persistência de Dados em JSON
- ✅ Interface Gráfica (Swing)
- ✅ Autenticação (Banco de Dados SQLite)

### v0.9 (02/03/2026)

- ✅ Fix: Persistência de Level funcionando 100%
- ✅ Recarregamento de personagens no lobby

---

## 📖 Documentação Completa

Para modificações avançadas, leia:

**→ GUIA_DESENVOLVIMENTO.md** (Este é o documento master)

---

**Versão**: 1.0  
**Última Atualização**: 03/03/2026  
**Compatibilidade**: Java 25+  
**Licença**: Projeto Pessoal

---

### 🎮 Pronto para Desenvolver?

1. Leia **GUIA_DESENVOLVIMENTO.md**
2. Escolha uma modificação
3. Siga os exemplos de código
4. Recompile e teste
5. Aproveite o desenvolvimento! 🚀

Bom código! 💻✨
