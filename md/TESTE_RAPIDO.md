# 🧪 Guia Rápido de Testes - Kronus Rift

> **Teste as alterações rapidamente antes de fazer push**

---

## ✅ Teste 1: Login Básico (2 minutos)

```
1. Inicie a aplicação: java -cp "lib/*;out" ui.Xlogin
2. Use credenciais de teste:
   └─ Usuário: teste123
   └─ Senha: teste123
3. Verifique se entra no lobby
4. ✅ Esperado: Ver tela de "Seus Personagens"
```

---

## ✅ Teste 2: Criar Personagem (3 minutos)

```
1. Clique em "Criar Novo Personagem"
2. Digite um nome: "TestChar123"
3. Selecione raça: "Humano"
4. Selecione classe: "Guerreiro"
5. Clique em "Criar Personagem"
6. ✅ Esperado: 
   - Personagem aparece na lista
   - Mostra "[Lv. 1]"
   - Mostra ícone da raça e classe
```

---

## ✅ Teste 3: Entrar no Jogo (5 minutos)

```
1. Clique em "Entrar" no seu personagem
2. ✅ Esperado:
   - Lobby fecha
   - Abre tela do jogo
   - Mostra nome: "TestChar123"
   - Mostra mundo: "Floresta de Eldoria"
   - Mostra stats no lado direito:
     * Level: 1
     * HP: 136 / 136
     * Mana: 70 / 70
     * STR: 18 | AGI: 12 | INT: 10
     * Inimigos derrotados: 0
```

---

## ✅ Teste 4: Habilidades Especiais (5 minutos)

```
1. Na tela de jogo, procure seção "⚡ HABILIDADES ESPECIAIS"
2. ✅ Deverá ver botões:
   └─ Guerreiro: "Golpe Poderoso" e "Fúria Berserker"
   └─ Caçador: "Tiro Preciso" e "Chuva de Flechas"
   └─ Bruxo: "Bola de Fogo" e "Maldição Sombria"

3. Clique em "Golpe Poderoso"
4. ✅ Esperado:
   - Mana reduz de 70 para 50
   - Log mostra: "✨ Golpe Poderoso usado contra o [inimigo]!"
   - Dano mostrado é > 36 (1.5x multiplicador)
   - Botão fica desativado por 3 segundos
```

---

## ✅ Teste 5: Combat Basic (5 minutos)

```
1. Clique em "Lutar contra Inimigo" (botão vermelho)
2. ✅ Esperado:
   - HP reduz após cada ataque do inimigo
   - Log mostra: "⚔️ Você lutas contra um [tipo]!"
   - Log mostra: "✓ Vitória! Ganhou 100 XP e 30 HP"
   - Level sobe automaticamente se XP ≥ necessário

3. Teste 3-4 vezes para accumular XP
4. ✅ Quando chegar ao XP necessário:
   - Level sobe de 1 para 2
   - Stats aumentam:
     * STR: 18 → 20
     * AGI: 12 → 13
     * INT: 10 → 12
```

---

## ✅ Teste 6: Mana e Cooldown (3 minutos)

```
1. Com Mana = 70
2. Use habilidade "Golpe Poderoso" (custa 20 mana)
3. Mana agora = 50
4. Imediatamente clique em "Golpe Poderoso" de novo
5. ✅ Esperado: 
   - Log mostra: "❌ Não pode usar Golpe Poderoso!"
   - "Mana insuficiente ou em cooldown."
6. Espere 3 segundos
7. ✅ Poderá usar novamente
```

---

## ✅ Teste 7: Salvar e Reabrir Lobby (5 minutos)

```
1. Seu personagem agora está Lvl 2 ou 3
2. Clique em "Sair do Jogo"
3. Escolha "Sim" para confirmar
4. ✅ Esperado:
   - Log mostra: "💾 Progresso salvo com sucesso!"
   - Jogo fecha
   - Lobby reabre

5. Procure seu personagem na lista
6. ✅ CRITICAL TEST:
   - Nome mostra: "[Lv. 2]" ou "[Lv. 3]" (não [Lv. 1]!)
   - Se mostrar [Lv. 1], persistência está quebrada

7. Clique em "Entrar" novamente
8. ✅ Stats devem ser iguais ao anterior:
   - Mesmo Level
   - Mesmo STR, AGI, INT
```

---

## ✅ Teste 8: Deletar Personagem (2 minutos)

```
1. Clique em "Deletar" no seu personagem
2. Confirme: "Sim, deletar"
3. ✅ Esperado:
   - Personagem desaparece da lista
   - Log mostra: "✓ Personagem deletado com sucesso!"
```

---

## ✅ Teste 9: Balanceamento de Dano (10 minutos)

```
Guerreiro Level 1:
- STR: 18, AGI: 12
- Dano base: 18 + (12/2) = 24
- Golpe Poderoso: 24 × 1.5 = 36 dano esperado

Teste:
1. Crie guerreiro
2. Clique em "Golpe Poderoso"
3. Log deve mostrar algo como:
   "✨ Golpe Poderoso usado contra o Goblin!"
   "Dano: 36 HP"

Se diferente:
- Verificar fórmula em GameWorld.java
- Verificar multiplier em Ability.java
```

---

## ✅ Teste 10: Múltiplas Classes (10 minutos)

```
Teste cada classe:

GUERREIRO:
- Crie personagem "Guerreiro1"
- Stats esperados: STR=21, AGI=10, INT=8
- Habilidades: Golpe Poderoso (1.5x), Fúria (2.0x)
- Dano esperado: 15-45

CAÇADOR:
- Crie personagem "Caçador1"
- Stats esperados: STR=16, AGI=22, INT=10
- Habilidades: Tiro (1.2x), Chuva (1.4x)
- Dano esperado: 18-30

BRUXO:
- Crie personagem "Bruxo1"
- Stats esperados: STR=10, AGI=12, INT=18
- Habilidades: Fogo (1.7x), Maldição (1.9x)
- Dano esperado: 16-30

✅ Resultado: Cada classe diferente, mas balanceadas
```

---

## 🐛 Bugs Comuns

### Bug 1: Habilidade não aparece

**Sintoma**: Seção "⚡ HABILIDADES ESPECIAIS" vazia

**Causa**: `initializeAbilities()` não foi chamado

**Fix**: Em `Character.java`, `initializeStats()`:
```java
private void initializeStats() {
    // ... code ...
    initializeAbilities();  // ← Verifique se está aqui
    // ... code ...
}
```

### Bug 2: Dano muito alto/baixo

**Sintoma**: Dano não corresponde à fórmula

**Causa**: Fórmula em `GameWorld.java` errada

**Fix**: Adicione debug:
```java
int baseDamage = playerCharacter.getStrength() + (playerCharacter.getAgility() / 2);
System.out.println("DEBUG: baseDamage = " + baseDamage);
System.out.println("DEBUG: finalDamage = " + finalDamage);
```

### Bug 3: Mana não reduz

**Sintoma**: Usa habilidade, mana não muda

**Causa**: `useAbility()` não debitou mana

**Fix**: Em `Character.java`, método `useAbility()`:
```java
public Ability useAbility(int index) {
    Ability ability = getAbility(index);
    if (ability != null && ability.canUse(this.mana)) {
        this.mana -= ability.getManaCost();  // ← Verifique
        ability.use();
        return ability;
    }
    return null;
}
```

### Bug 4: Level não salva

**Sintoma**: Sai do jogo com Lvl 5, volta com Lvl 1

**Causa**: `CharacterManager.java` não salvando level

**Fix**: Verifique `saveCharacters()`:
```java
json.append("    \"level\": ").append(ch.getLevel()).append(",\n");
```

### Bug 5: Compilação falha

**Sintoma**: `javac` erro ao compilar

**Causa**: Arquivo `Ability.java` não existe

**Fix**: Verifique se criou em `src/model/Ability.java`

---

## 📊 Teste de Performance

```
┌──────────────────┬────────┬──────────────┐
│ Ação             │ Tempo  │ Esperado     │
├──────────────────┼────────┼──────────────┤
│ Login            │ 2s     │ < 3s         │
│ Criar personagem │ 1s     │ < 2s         │
│ Entrar no jogo   │ 1s     │ < 2s         │
│ Combat          │ 0.1s    │ < 0.5s       │
│ Sair do jogo     │ 1s     │ < 2s         │
│ Salvar JSON      │ 0.2s   │ < 1s         │
│ Carregar JSON    │ 0.2s   │ < 1s         │
└──────────────────┴────────┴──────────────┘
```

---

## ✅ Checklist Final - Antes de Push

- [ ] Compilou sem erros: `javac -cp "lib/*" -d out src/**/*.java`
- [ ] Login funcionando
- [ ] Criar personagem funcionando
- [ ] Entrar no jogo funcionando
- [ ] Habilidades aparecem (⚡ seção)
- [ ] Habilidades usáveis (dano correto)
- [ ] Mana reduz ao usar
- [ ] Cooldown funciona (3 segundos para Golpe Poderoso)
- [ ] Combat mata inimigos
- [ ] Level-up automático
- [ ] Salvar progresso (sair do jogo)
- [ ] Reabrir lobby (personagem mostra level correto)
- [ ] Deletar personagem funciona
- [ ] Nenhum erro no console
- [ ] Balanceamento OK (dano não é OP)

---

## 🎯 Teste Rápido (15 minutos totais)

```bash
1. Compilar (2 min)
   javac -cp "lib/*" -d out src/**/*.java

2. Executar (1 min)
   java -cp "lib/*;out" ui.Xlogin

3. Criar char (2 min)
   - Login → Criar "Teste1" → Guerreiro

4. Entrar jogo (3 min)
   - Entrar → Ver habilidades → Usar "Golpe Poderoso"

5. Combat (3 min)
   - Lutar 3 vezes → Ver Level-up

6. Salvar (2 min)
   - Sair → Confirmar save → Reabrir lobby

7. Verificar (2 min)
   - Procura char → Verifica "[Lv. 2]" ou "[Lv. 3]"
   - ✅ Se tudo OK: Ready para push!
```

---

## 📞 Se Algo Falhar

1. **Verifique compilação**: Algum erro em `javac`?
2. **Teste isolado**: Teste uma feature por vez
3. **Debug output**: Adicione `System.out.println()`
4. **Revise o guia**: `GUIA_DESENVOLVIMENTO.md`
5. **Recompile limpo**: Delete `out/` e recompile

---

**Última Atualização**: 03/03/2026  
**Versão**: 1.0

Bom teste! 🎮✅
