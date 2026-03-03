# Recursos do Kronus Rift (Pixel Art & Assets)

Esta pasta contém todos os assets visuais do jogo Kronus Rift, incluindo sprites de personagens, inimigos e ambientes.

## 📁 Estrutura de Diretórios

```
res/
├── characters/
│   ├── humano/
│   │   ├── idle.png
│   │   ├── walk.png
│   │   └── attack.png
│   ├── goblin/
│   │   ├── idle.png
│   │   ├── walk.png
│   │   └── attack.png
│   └── elfo/
│       ├── idle.png
│       ├── walk.png
│       └── attack.png
├── enemies/
│   ├── goblin_enemy.png
│   ├── wolf.png
│   ├── forest_spirit.png
│   └── bandit.png
├── environments/
│   ├── forest_bg.png
│   ├── grass_tileset.png
│   └── trees.png
└── ui/
    ├── health_bar.png
    ├── mana_bar.png
    └── buttons.png
```

## 🎨 Especificações de Pixel Art

### Personagens (Sprites)
- **Dimensões**: 32x48 pixels (recomendado)
- **Formato**: PNG com transparência
- **Animações**:
  - `idle.png` - Parado (animação 4-6 frames)
  - `walk.png` - Caminhando (animação 6-8 frames)
  - `attack.png` - Atacando (animação 4-6 frames)

### Inimigos
- **Dimensões**: 32x32 ou 32x48 pixels
- **Formato**: PNG com transparência
- **Variações**:
  - Goblin (inimigo nível 1)
  - Lobo (inimigo nível 2)
  - Espírito Florestal (inimigo nível 3)
  - Bandido (inimigo nível 4)

### Ambientes
- **Tileset**: 32x32 pixels por tile
- **Formato**: PNG com transparência
- **Recomendado**: Tileset em atlas (múltiplos tiles em uma imagem)

## 🎬 Como Adicionar Assets no Código

### Carregar uma imagem:
```java
ImageIcon icon = new ImageIcon("res/characters/humano/idle.png");
Image img = icon.getImage();
```

### Integrar no GameWindow:
Edite `src/game/GameWindow.java` no método `createWorldPanel()` para renderizar sprites em vez do placeholder.

## 📥 Fontes Recomendadas para Pixel Art

- **OpenGameArt.org** - Asset packs gratuitos
- **itch.io** - Sprites 2D/pixel art
- **Aseprite** - Editor de pixel art profissional
- **Piskel** - Editor online gratuito
- **LibreSprite** - Software livre para pixel art

## 🎯 Próximas Etapas

1. Baixe ou crie seus sprites pixel art
2. Coloque-os nos diretórios correspondentes em `/res/`
3. Atualize o código Java para carregar e exibir os sprites
4. Implemente animações de sprites (frame-by-frame)

## ⚠️ Dicas

- Use cores consistentes (paleta de cores limitada é melhor para pixel art)
- Mantenha a proporção 1:1 para pixel art puro (sem anti-aliasing)
- Teste os sprites em diferentes resoluções
- Considere criar uma folha de sprites (atlas) para otimizar carregamento

---

**Autor**: Equipe Kronus Rift  
**Data**: Março 2026  
**Status**: 🚧 Em Desenvolvimento
