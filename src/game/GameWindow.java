package game;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import model.Character;
import util.CharacterManager;

/**
 * Interface visual do jogo - Primeiro mundo.
 * Mostra o mapa, stats do personagem e opções de ação.
 */
public class GameWindow extends JFrame {
    private final GameWorld gameWorld;
    private final Character character;
    private final int userId;
    private final JFrame lobbyWindow;
    private JLabel characterNameLabel;
    private JLabel levelLabel;
    private JLabel statsLabel;
    private JLabel healthLabel;
    private JLabel manaLabel;
    private JLabel enemiesLabel;
    private JPanel worldPanel;
    private JTextArea logArea;
    private BufferedImage mapImage;

    public GameWindow(Character character, int userId, JFrame lobbyWindow) {
        super("Kronus Rift - " + character.getName());
        this.character = character;
        this.userId = userId;
        this.lobbyWindow = lobbyWindow;
        this.gameWorld = new GameWorld(character);
        initUI();
    }

    // Construtor antigo para compatibilidade (será removido)
    public GameWindow(Character character) {
        this(character, 0, null);
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        getContentPane().setLayout(new BorderLayout(12, 12));
        getContentPane().setBackground(new Color(20, 20, 30));

        // --- TOP: Header ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(40, 40, 60));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        characterNameLabel = new JLabel(character.getName() + " - " + character.getRace() + " " + character.getClazz());
        characterNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        characterNameLabel.setForeground(new Color(255, 200, 100));

        JLabel worldLabel = new JLabel("📍 " + gameWorld.getWorldName() + " - " + gameWorld.getWorldDescription());
        worldLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        worldLabel.setForeground(new Color(150, 150, 150));

        headerPanel.add(characterNameLabel);
        headerPanel.add(worldLabel);
        getContentPane().add(headerPanel, BorderLayout.NORTH);

        // --- CENTER: Split (World + Stats) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBackground(new Color(20, 20, 30));
        splitPane.setDividerLocation(600);

        // --- LEFT: World/Game Area ---
        worldPanel = createWorldPanel();
        splitPane.setLeftComponent(worldPanel);

        // --- RIGHT: Stats Panel ---
        JPanel statsPanel = createStatsPanel();
        splitPane.setRightComponent(statsPanel);

        getContentPane().add(splitPane, BorderLayout.CENTER);

        // --- BOTTOM: Action Buttons ---
        JPanel actionPanel = createActionPanel();
        getContentPane().add(actionPanel, BorderLayout.SOUTH);

        // Listener para fechar graciosamente
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveAndExit();
            }
        });

        updateStatsDisplay();
    }

    private JPanel createWorldPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 50));
        panel.setLayout(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // --- Carrega a imagem do mapa (pixel art) ---
        try {
            File mapFile = new File("res/map-eldora.png");
            if (mapFile.exists()) {
                mapImage = ImageIO.read(mapFile);
            } else {
                mapImage = null;
                System.err.println("Aviso: res/map-eldora.png não encontrado");
            }
        } catch (IOException ex) {
            System.err.println("Erro ao carregar res/map-eldora.png: " + ex.getMessage());
            mapImage = null;
        }

        // --- Map/Canvas Area (espaço para pixel art) ---
        JPanel mapArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                if (mapImage != null) {
                    // ⭐ RENDERING HINTS para PIXEL ART
                    // Desliga anti-aliasing e usa NEAREST_NEIGHBOR para manter pixels nítidos
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                        RenderingHints.VALUE_ANTIALIAS_OFF);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, 
                        RenderingHints.VALUE_RENDER_SPEED);

                    int imgWidth = mapImage.getWidth();
                    int imgHeight = mapImage.getHeight();
                    int panelWidth = getWidth();
                    int panelHeight = getHeight();

                    // Calcula escala mantendo proporção (prefere escala inteira para pixel art)
                    double scaleX = panelWidth / (double) imgWidth;
                    double scaleY = panelHeight / (double) imgHeight;
                    double scale = Math.min(scaleX, scaleY);
                    int intScale = Math.max(1, (int) Math.floor(scale));
                    double finalScale = intScale;

                    int drawWidth = (int) (imgWidth * finalScale);
                    int drawHeight = (int) (imgHeight * finalScale);
                    int x = (panelWidth - drawWidth) / 2;
                    int y = (panelHeight - drawHeight) / 2;

                    // Desenha a imagem escalada com AffineTransform
                    AffineTransform at = AffineTransform.getTranslateInstance(x, y);
                    at.scale(finalScale, finalScale);
                    g2d.drawImage(mapImage, at, null);

                    // Label em cima da imagem
                    g2d.setColor(new Color(100, 100, 100, 150));
                    g2d.setFont(new Font("Arial", Font.ITALIC, 12));
                    g2d.drawString("Floresta de Eldoria", x + 10, y + 20);

                } else {
                    // Fallback: fundo degradado + grid (se imagem não encontrada)
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                        RenderingHints.VALUE_ANTIALIAS_ON);

                    GradientPaint gp = new GradientPaint(0, 0, new Color(50, 80, 40),
                            getWidth(), getHeight(), new Color(30, 60, 30));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());

                    // Grid placeholder
                    g2d.setColor(new Color(100, 150, 100, 30));
                    g2d.setStroke(new BasicStroke(1));
                    for (int xx = 0; xx < getWidth(); xx += 40) {
                        g2d.drawLine(xx, 0, xx, getHeight());
                    }
                    for (int yy = 0; yy < getHeight(); yy += 40) {
                        g2d.drawLine(0, yy, getWidth(), yy);
                    }

                    // Placeholder para sprite do personagem
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;
                    g2d.setColor(new Color(255, 200, 100));
                    g2d.fillRect(centerX - 16, centerY - 32, 32, 48);
                    g2d.setColor(new Color(200, 150, 50));
                    g2d.drawRect(centerX - 16, centerY - 32, 32, 48);

                    // Texto: espaço para pixel art
                    g2d.setColor(new Color(150, 150, 150, 100));
                    g2d.setFont(new Font("Arial", Font.ITALIC, 14));
                    g2d.drawString("[SPRITE DO PERSONAGEM - Adicione pixel art em /res/]", 20, 30);
                    g2d.drawString("Floresta de Eldoria", 20, getHeight() - 20);
                }
            }
        };
        mapArea.setBackground(new Color(30, 60, 30));
        mapArea.setPreferredSize(new Dimension(600, 400));

        panel.add(mapArea, BorderLayout.CENTER);

        // --- Log Area ---
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        logArea.setBackground(new Color(20, 20, 30));
        logArea.setForeground(new Color(150, 255, 150));
        logArea.setText("=== Log do Jogo ===\n");
        logArea.append("Bem-vindo a Floresta de Eldoria!\n");
        logArea.append("Use os botões de ação para explorar e lutar.\n");

        JScrollPane scrollLog = new JScrollPane(logArea);
        scrollLog.setPreferredSize(new Dimension(600, 100));
        scrollLog.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 120), 1));

        panel.add(scrollLog, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(40, 40, 60));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // --- Character Info ---
        JLabel infoTitle = new JLabel("📊 Estatísticas");
        infoTitle.setFont(new Font("Arial", Font.BOLD, 16));
        infoTitle.setForeground(new Color(255, 200, 100));
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(infoTitle);
        panel.add(Box.createVerticalStrut(12));

        // Level
        levelLabel = new JLabel("Level: 1");
        levelLabel.setFont(new Font("Arial", Font.BOLD, 14));
        levelLabel.setForeground(new Color(100, 200, 100));
        levelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(levelLabel);
        panel.add(Box.createVerticalStrut(4));

        // Health Bar
        healthLabel = new JLabel("❤️ HP: 100 / 100");
        healthLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        healthLabel.setForeground(new Color(200, 100, 100));
        healthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(healthLabel);

        JProgressBar healthBar = new JProgressBar(0, 100);
        healthBar.setValue(100);
        healthBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        healthBar.setBackground(new Color(50, 50, 50));
        healthBar.setForeground(new Color(200, 50, 50));
        panel.add(healthBar);
        panel.add(Box.createVerticalStrut(8));

        // Mana Bar
        manaLabel = new JLabel("💙 Mana: 100 / 100");
        manaLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        manaLabel.setForeground(new Color(100, 150, 200));
        manaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(manaLabel);

        JProgressBar manaBar = new JProgressBar(0, 100);
        manaBar.setValue(100);
        manaBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        manaBar.setBackground(new Color(50, 50, 50));
        manaBar.setForeground(new Color(100, 150, 200));
        panel.add(manaBar);
        panel.add(Box.createVerticalStrut(12));

        // Stats
        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statsLabel.setForeground(new Color(180, 180, 200));
        statsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statsLabel);
        panel.add(Box.createVerticalStrut(12));

        // Enemies Defeated
        enemiesLabel = new JLabel("👹 Inimigos Derrotados: 0");
        enemiesLabel.setFont(new Font("Arial", Font.BOLD, 12));
        enemiesLabel.setForeground(new Color(200, 100, 200));
        enemiesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(enemiesLabel);
        panel.add(Box.createVerticalStrut(20));

        // --- Combat Buttons ---
        JLabel combatTitle = new JLabel("⚔️ Combate");
        combatTitle.setFont(new Font("Arial", Font.BOLD, 14));
        combatTitle.setForeground(new Color(255, 150, 100));
        combatTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(combatTitle);
        panel.add(Box.createVerticalStrut(8));

        JButton fightButton = new JButton("Lutar contra Inimigo");
        fightButton.setFont(new Font("Arial", Font.BOLD, 12));
        fightButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        fightButton.setBackground(new Color(200, 50, 50));
        fightButton.setForeground(Color.WHITE);
        fightButton.setFocusPainted(false);
        fightButton.addActionListener(e -> fight());
        panel.add(fightButton);
        panel.add(Box.createVerticalStrut(8));

        JButton healButton = new JButton("Descansar (Curar)");
        healButton.setFont(new Font("Arial", Font.BOLD, 12));
        healButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        healButton.setBackground(new Color(100, 150, 100));
        healButton.setForeground(Color.WHITE);
        healButton.setFocusPainted(false);
        healButton.addActionListener(e -> rest());
        panel.add(healButton);
        panel.add(Box.createVerticalStrut(20));

        // --- Exit Button ---
        JButton exitButton = new JButton("Sair do Jogo");
        exitButton.setFont(new Font("Arial", Font.BOLD, 12));
        exitButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        exitButton.setBackground(new Color(100, 100, 150));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.addActionListener(e -> saveAndExit());
        panel.add(exitButton);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(40, 40, 60));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ⭐ PAINEL DE HABILIDADES ESPECIAIS
        JPanel abilitiesPanel = new JPanel();
        abilitiesPanel.setBackground(new Color(40, 40, 60));
        abilitiesPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 6));
        abilitiesPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 150, 50), 1),
            "⚡ Habilidades Especiais",
            0, 0,
            new Font("Arial", Font.BOLD, 11),
            new Color(255, 150, 50)
        ));

        java.util.List<model.Ability> abilities = character.getAbilities();
        for (int i = 0; i < abilities.size(); i++) {
            model.Ability ability = abilities.get(i);
            final int abilityIndex = i;

            JButton abilityBtn = new JButton(ability.getName());
            abilityBtn.setFont(new Font("Arial", Font.BOLD, 10));
            abilityBtn.setBackground(new Color(100, 150, 200));
            abilityBtn.setForeground(Color.WHITE);
            abilityBtn.setFocusPainted(false);
            abilityBtn.setPreferredSize(new Dimension(140, 35));
            
            String tooltip = String.format(
                "<html>%s<br/>Mana: %d | Dano: %.0f%% | CD: %.1fs</html>",
                ability.getDescription(),
                ability.getManaCost(),
                ability.getDamageMultiplier() * 100,
                ability.getCooldownMs() / 1000.0
            );
            abilityBtn.setToolTipText(tooltip);

            abilityBtn.addActionListener(e -> {
                model.Ability used = character.useAbility(abilityIndex);
                if (used != null) {
                    String[] enemies = {"Goblin", "Lobo", "Espirito Florestal", "Bandido"};
                    String enemy = enemies[(int) (Math.random() * enemies.length)];
                    gameWorld.fightEnemy(enemy, abilityIndex);
                    
                    int damage = used.calculateDamage(character.getStrength() + (character.getAgility() / 2));
                    logArea.append("\n✨ " + used.getName() + " usado contra o " + enemy + "!\n");
                    logArea.append("   Dano: " + damage + " HP\n");

                    if (character.getHealth() > 0) {
                        logArea.append("   ✓ Vitória! Ganhou XP e cura.\n");
                    } else {
                        logArea.append("   ✗ Você foi derrotado!\n");
                    }
                    
                    updateStatsDisplay();
                } else {
                    logArea.append("\n❌ Não pode usar " + ability.getName() + "!\n");
                    logArea.append("   Mana insuficiente ou em cooldown.\n");
                }
                updateStatsDisplay();
            });

            abilitiesPanel.add(abilityBtn);
        }

        mainPanel.add(abilitiesPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // PAINEL DE COMBATE NORMAL
        JPanel combatPanel = new JPanel();
        combatPanel.setBackground(new Color(40, 40, 60));
        combatPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        combatPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 100, 100), 1),
            "⚔️ Combate",
            0, 0,
            new Font("Arial", Font.BOLD, 11),
            new Color(200, 100, 100)
        ));

        JButton fightBtn = new JButton("Lutar contra Inimigo");
        fightBtn.setFont(new Font("Arial", Font.BOLD, 12));
        fightBtn.setBackground(new Color(200, 80, 80));
        fightBtn.setForeground(Color.WHITE);
        fightBtn.setFocusPainted(false);
        fightBtn.setPreferredSize(new Dimension(140, 35));
        fightBtn.addActionListener(e -> {
            fight();
            updateStatsDisplay();
        });

        JButton restBtn = new JButton("Descansar (Curar)");
        restBtn.setFont(new Font("Arial", Font.BOLD, 12));
        restBtn.setBackground(new Color(80, 180, 80));
        restBtn.setForeground(Color.WHITE);
        restBtn.setFocusPainted(false);
        restBtn.setPreferredSize(new Dimension(140, 35));
        restBtn.addActionListener(e -> {
            character.heal(50);
            logArea.append("\n💤 Você descansou e recuperou 50 HP.\n");
            updateStatsDisplay();
        });

        JButton exitBtn = new JButton("Sair do Jogo");
        exitBtn.setFont(new Font("Arial", Font.BOLD, 12));
        exitBtn.setBackground(new Color(100, 100, 150));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFocusPainted(false);
        exitBtn.setPreferredSize(new Dimension(140, 35));
        exitBtn.addActionListener(e -> saveAndExit());

        combatPanel.add(fightBtn);
        combatPanel.add(restBtn);
        combatPanel.add(exitBtn);

        mainPanel.add(combatPanel);

        // Timer de sessão
        mainPanel.add(Box.createVerticalStrut(10));
        JLabel actionLabel = new JLabel("Duração da Sessão: 00:00:00");
        actionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        actionLabel.setForeground(new Color(200, 200, 200));
        actionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(actionLabel);

        Timer timer = new Timer(1000, e -> {
            long duration = gameWorld.getSessionDuration();
            long hours = duration / 3600;
            long minutes = (duration % 3600) / 60;
            long seconds = duration % 60;
            actionLabel.setText(String.format("Duração da Sessão: %02d:%02d:%02d", hours, minutes, seconds));
        });
        timer.start();

        return mainPanel;
    }

    private void fight() {
        if (character.getHealth() <= 0) {
            JOptionPane.showMessageDialog(this, "Você está fraco demais para lutar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] enemies = {"Goblin", "Lobo", "Espirito Florestal", "Bandido"};
        String enemy = enemies[(int) (Math.random() * enemies.length)];

        gameWorld.fightEnemy(enemy);
        logArea.append("\n⚔️ Você lutas contra um " + enemy + "!\n");

        if (character.getHealth() > 0) {
            logArea.append("✓ Vitória! Você derrotou o " + enemy + "!\n");
            logArea.append("  Ganhou 100 XP e 30 HP de cura.\n");
        } else {
            logArea.append("✗ Derrota! Você foi derrotado pelo " + enemy + "!\n");
        }

        updateStatsDisplay();
    }

    private void rest() {
        character.heal(50);
        logArea.append("\n😴 Você descansa e se recupera...\n");
        logArea.append("✓ Curado! HP restaurado (+50)\n");
        updateStatsDisplay();
    }

    private void updateStatsDisplay() {
        levelLabel.setText("Level: " + character.getLevel());
        healthLabel.setText("❤️ HP: " + character.getHealth() + " / " + (100 + character.getStrength() * 2));
        manaLabel.setText("💙 Mana: " + character.getMana() + " / " + (50 + character.getIntelligence() * 2));
        statsLabel.setText(String.format("⚔️ STR: %d  |  🏃 AGI: %d  |  🧠 INT: %d", 
            character.getStrength(), character.getAgility(), character.getIntelligence()));
        enemiesLabel.setText("👹 Inimigos Derrotados: " + gameWorld.getEnemiesDefeated());
    }

    private void saveAndExit() {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja sair do jogo?\nSeu progresso será salvo.", 
                "Sair", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            logArea.append("\n📍 Saindo do jogo...\n");
            
            // Salva progresso do personagem
            try {
                CharacterManager.saveCharacters(userId, java.util.List.of(character));
                logArea.append("💾 Progresso salvo com sucesso!\n");
            } catch (IOException ex) {
                logArea.append("⚠️ Erro ao salvar progresso: " + ex.getMessage() + "\n");
            }
            
            // Fecha a janela do jogo
            dispose();
            
            // Reabre o lobby
            if (lobbyWindow != null) {
                lobbyWindow.setVisible(true);
            }
        }
    }
}
