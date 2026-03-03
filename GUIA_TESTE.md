# 🎮 GUIA DE TESTE - Sistema de Mundo & Level do Kronus Rift

## ⚡ Quick Start (5 minutos)

### Passo 1: Compilar o Projeto
```powershell
cd D:\xlogin-project-java\Project-xlogin-java

# Compile tudo
javac -cp "lib/*" -d out (Get-ChildItem -Recurse -Filter *.java -Path src | % FullName)
```

### Passo 2: Executar a Aplicação
```powershell
java -cp "out;lib/*" ui.Xlogin
```

### Passo 3: Fazer Login
- **Username**: teteu (ou seu usuário)
- **Password**: sua senha
- Clique em "Entrar"

### Passo 4: Criar um Personagem
Na janela do **Kronus Rift - Lobby**:
1. Digite um nome (ex: "Herói")
2. Selecione Raça: **Elfo**
3. Selecione Classe: **Guerreiro** 
4. Clique em **"Criar Personagem"**
5. Veja o personagem aparecer na lista à direita

### Passo 5: Entrar no Jogo
1. Clique em **"Entrar"** no seu personagem
2. A janela do **Kronus Rift - Mundo Inicial** abrirá
3. Você verá:
   - Canvas com grid (espaço para pixel art)
   - Seus stats à direita
   - Log de ações em baixo

### Passo 6: Testar o Sistema de Combat
1. Clique em **"Lutar contra Inimigo"**
2. Veja no log: `⚔️ Você lutas contra um Goblin!`
3. Se vencer: stats atualizam, XP é ganho
4. Se o Level subir: veja os stats aumentarem (STR +2, AGI +1, INT +2)

### Passo 7: Sair do Jogo
Clique em **"Sair do Jogo"** → volta ao lobby
Seus stats foram salvos em `/characters/user_4_characters.json`

---

## 📝 Checklist de Funcionalidades

### ✅ Login & Lobby
- [ ] Fazer login funciona
- [ ] Lobby abre após login bem-sucedido
- [ ] Aba de login fecha automaticamente

### ✅ Criação de Personagem
- [ ] Campo de nome valida se vazio
- [ ] Valida nomes duplicados
- [ ] Dropdown de raça e classe funcionam
- [ ] Personagem aparece na lista
- [ ] Stats iniciais corretos (STR: 18, AGI: 12, INT: 10, Level: 1)

### ✅ Listagem de Personagens
- [ ] Personagens aparecem com ícones (emojis)
- [ ] Botão "Entrar" abre GameWindow
- [ ] Botão "Deletar" remove da lista e JSON

### ✅ GameWindow (Mundo Inicial)
- [ ] Canvas renderiza corretamente
- [ ] Log aparece em tempo real
- [ ] Stats mostram corretamente
- [ ] Health bar e Mana bar trabalham

### ✅ Sistema de Combat
- [ ] Botão "Lutar" simula combate
- [ ] Dano é calculado (STR + AGI/2)
- [ ] Vitória restaura 30 HP
- [ ] XP é adicionado
- [ ] Log mostra resultado

### ✅ Sistema de Level-Up
- [ ] XP suficiente → Level-Up automático
- [ ] Level aumenta (1 → 2)
- [ ] STR aumenta (+2)
- [ ] AGI aumenta (+1)
- [ ] INT aumenta (+2)
- [ ] HP regenera
- [ ] Mana regenera

### ✅ Persistência
- [ ] Arquivo JSON cria em `/characters/`
- [ ] Dados salvam ao criar personagem
- [ ] Dados carregam ao abrir lobby
- [ ] Personagem deletado remove do JSON

### ✅ Design & UX
- [ ] Cores temáticas (azul escuro + laranja ouro)
- [ ] Fontes legíveis
- [ ] Layout intuitivo
- [ ] Botões respondem corretamente

---

## 🐛 Troubleshooting

### Problema: "ClassNotFoundException: ui.Xlogin"
**Solução**: Certifique-se de compilar:
```powershell
javac -cp "lib/*" -d out src\**\*.java
```

### Problema: "Package model does not exist"
**Solução**: Compile com classpath completo:
```powershell
javac -cp "lib/*:out" -d out src\ui\*.java
```

### Problema: Personagem não aparece na lista
**Solução**: Verifique se `/characters/` foi criado. Se não, o programa criará automaticamente.

### Problema: GameWindow não abre
**Solução**: Verifique erros no console. Pode ser falta de tela/display.

### Problema: XP/Level não atualiza
**Solução**: Compile novamente com Character.java:
```powershell
javac -cp "lib/*" -d out src\model\Character.java src\game\GameWindow.java
```

---

## 🎯 Testes Avançados

### Teste 1: Múltiplos Personagens
1. Crie 3 personagens diferentes (raças/classes)
2. Verifique se cada um tem stats independentes
3. Levelup um e verifique se os outros não mudam

### Teste 2: Persistência
1. Crie personagem "TestChar"
2. Feche a aplicação completamente
3. Abra novamente e faça login
4. Verifique se "TestChar" ainda existe no lobby

### Teste 3: Level-Up em Sequência
1. Lute 15+ vezes para ganhar XP
2. Veja o log de level-ups
3. Stats devem aumentar progressivamente

### Teste 4: Morte em Combate
1. Lute até deixar HP muito baixo
2. Lute uma última vez
3. Veja mensagem de derrota
4. Descansar deve restaurar HP

### Teste 5: Deletar & Recriar
1. Crie personagem "Delete-Me"
2. Clique "Deletar" com confirmação
3. Verifique se saiu da lista
4. Crie outro com mesmo nome
5. Verifique se é um novo (createdAt diferente)

---

## 📊 Exemplo de JSON Gerado

Arquivo: `/characters/user_4_characters.json`

```json
[
  {
    "name": "Herói",
    "race": "Elfo",
    "clazz": "Guerreiro",
    "level": 3,
    "strength": 22,
    "agility": 14,
    "intelligence": 14,
    "health": 144,
    "mana": 128,
    "experience": 2100,
    "createdAt": 1741028154000
  }
]
```

---

## 📸 Screenshots Esperados

### Tela 1: Lobby
```
┌─────────────────────────────────────────┐
│ Kronus Rift                             │
│ Bem-vindo ao Lobby - Criação de...      │
├─────────────────────┬───────────────────┤
│ Criar Novo...       │ Seus Personagens: │
│ Nome: [_______]     │                   │
│ Raça: [Elfo v]      │ ✦ Herói           │
│ Classe: [Guerreiro] │ 🧝 Elfo | ⚔️ Guer │
│ [Criar Personagem]  │ [Entrar] [Deletar]│
│                     │                   │
│                     │ ✦ Mago            │
│                     │ 👤 Humano | 🔮 Bru│
│                     │ [Entrar] [Deletar]│
└─────────────────────┴───────────────────┘
```

### Tela 2: GameWindow
```
┌─────────────────────────────────────────┐
│ Herói - Elfo Guerreiro                  │
│ 📍 Floresta de Eldoria                  │
├──────────────────────┬───────────────────┤
│ [Mapa com Grid]      │ Level: 3          │
│ [Sprite do char]     │ ❤️ HP: 160/144    │
│                      │ 💙 Mana: 128/128  │
│ Log:                 │ ⚔️ STR: 22        │
│ > Entraste...        │ 🏃 AGI: 14        │
│ > Derrotaste Goblin  │ 🧠 INT: 14        │
│ > Level Up! 2 → 3    │                   │
│                      │ 👹 Inimigos: 15   │
│                      │ [⚔️ Lutar]        │
│                      │ [😴 Descansar]    │
│                      │ [Sair]            │
└──────────────────────┴───────────────────┘
Duração: 00:15:42
```

---

## 🚀 Próximas Etapas (Opcionais)

1. **Adicionar Pixel Art**:
   - Baixe sprites de https://itch.io
   - Coloque em `/res/characters/elfo/`
   - Edite GameWindow para renderizar imagens

2. **Mais Inimigos**:
   - Crie novos tipos em GameWorld
   - Aumente dificuldade por nível

3. **Persistência em DB**:
   - Migre de JSON para SQLite
   - Salve progressão em banco real

4. **Multiplayer**:
   - Implemente PvP básico
   - Chat com outros jogadores

---

## 💡 Dicas de Desenvolvimento

- Use o terminal para capturar logs: `java ... ui.Xlogin 2>&1 | Tee-Object game.log`
- Pressione F12 em algumas janelas para abrir inspetor (se Swing debug ativado)
- JSON é salvo automaticamente, edite `/characters/*.json` para testar persistência
- Adicione `println()` em GameWorld.java para debugar combate

---

**Divirta-se testando! 🎮✨**
