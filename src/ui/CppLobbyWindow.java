package ui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import javax.swing.*;
import model.Character;
import util.CharacterManager;

/**
 * Janela de Lobby do Kronus Rift estilo WoW.
 * Interface para criação, listagem e gerenciamento de personagens.
 */
public class CppLobbyWindow extends JFrame {
    private final String userId;
    private final Process process;
    private PrintWriter processWriter;
    private JPanel characterListPanel;
    private JTextField nameField;
    private JComboBox<String> raceBox;
    private JComboBox<String> classBox;
    private List<Character> characters;

    public CppLobbyWindow(Process process, String userId, String execPath) {
        super("Kronus Rift - Lobby");
        this.process = process;
        this.userId = userId;
        this.characters = CharacterManager.loadCharacters(userId);
        initUI(userId);
        hookProcessStreams();
    }

    private void initUI(String userId) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        getContentPane().setLayout(new BorderLayout(12, 12));
        getContentPane().setBackground(new Color(20, 20, 30));

        // --- HEADER ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(40, 40, 60));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titleLabel = new JLabel("Kronus Rift");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(255, 200, 100));

        JLabel subtitleLabel = new JLabel("Bem-vindo ao Lobby - Kronus Rift MMORPG ");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(150, 150, 150));

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        getContentPane().add(headerPanel, BorderLayout.NORTH);

        // --- CENTER: Split Panel (Creation + Character List) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBackground(new Color(20, 20, 30));
        splitPane.setDividerLocation(350);

        // --- LEFT: Creation Panel ---
        JPanel creationPanel = createCreationPanel();
        splitPane.setLeftComponent(creationPanel);

        // --- RIGHT: Character List Panel ---
        characterListPanel = new JPanel();
        characterListPanel.setBackground(new Color(20, 20, 30));
        characterListPanel.setLayout(new BoxLayout(characterListPanel, BoxLayout.Y_AXIS));
        characterListPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel charactersLabel = new JLabel("Seus Personagens:");
        charactersLabel.setFont(new Font("Arial", Font.BOLD, 20));
        charactersLabel.setForeground(new Color(255, 200, 100));

        JScrollPane scrollArea = new JScrollPane(characterListPanel);
        scrollArea.setBackground(new Color(20, 20, 30));
        scrollArea.getViewport().setBackground(new Color(20, 20, 30));
        scrollArea.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 120), 1));

        JPanel rightPanel = new JPanel(new BorderLayout(6, 6));
        rightPanel.setBackground(new Color(20, 20, 30));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        rightPanel.add(charactersLabel, BorderLayout.NORTH);
        rightPanel.add(scrollArea, BorderLayout.CENTER);

        splitPane.setRightComponent(rightPanel);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // Carrega personagens na lista
        refreshCharacterList();

        // garantir que o processo seja finalizado ao fechar a janela
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (process.isAlive()) {
                        process.destroy();
                        process.waitFor();
                    }
                } catch (InterruptedException ignored) {
                }
            }
            
            @Override
            public void windowDeiconified(WindowEvent e) {
                // Quando a janela volta a ficar visível, recarrega os personagens
                characters = CharacterManager.loadCharacters(userId);
                refreshCharacterList();
            }
            
            @Override
            public void windowActivated(WindowEvent e) {
                // Quando a janela ganha foco (pode ter voltado de outro programa), recarrega
                characters = CharacterManager.loadCharacters(userId);
                refreshCharacterList();
            }
        });
    }

    private JPanel createCreationPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(40, 40, 60));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel creationTitle = new JLabel("Criar Novo Personagem");
        creationTitle.setFont(new Font("Arial", Font.BOLD, 20));
        creationTitle.setForeground(new Color(255, 200, 100));
        creationTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(creationTitle);
        panel.add(Box.createVerticalStrut(16));

        // Nome
        JLabel nameLabel = new JLabel("Nome do Personagem:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(new Color(200, 200, 200));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(nameLabel);

        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 12));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        nameField.setBackground(new Color(30, 30, 40));
        nameField.setForeground(new Color(200, 200, 200));
        nameField.setCaretColor(Color.WHITE);
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(12));

        // Raça
        JLabel raceLabel = new JLabel("Raça:");
        raceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        raceLabel.setForeground(new Color(200, 200, 200));
        raceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(raceLabel);

        raceBox = new JComboBox<>(new String[]{"Humano 👤", "Goblin 👹", "Elfo 🧝"});
        raceBox.setFont(new Font("Arial", Font.PLAIN, 12));
        raceBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        raceBox.setBackground(new Color(30, 30, 40));
        raceBox.setForeground(new Color(200, 200, 200));
        panel.add(raceBox);
        panel.add(Box.createVerticalStrut(12));

        // Classe
        JLabel classLabel = new JLabel("Classe:");
        classLabel.setFont(new Font("Arial", Font.BOLD, 20));
        classLabel.setForeground(new Color(200, 200, 200));
        classLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(classLabel);

        classBox = new JComboBox<>(new String[]{"Caçador 🏹", "Guerreiro ⚔️", "Bruxo 🔮"});
        classBox.setFont(new Font("Arial", Font.PLAIN, 12));
        classBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        classBox.setBackground(new Color(30, 30, 40));
        classBox.setForeground(new Color(200, 200, 200));
        panel.add(classBox);
        panel.add(Box.createVerticalStrut(20));

        // Create button
        JButton createButton = new JButton("Criar Personagem");
        createButton.setFont(new Font("Arial", Font.BOLD, 14));
        createButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        createButton.setBackground(new Color(200, 100, 50));
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        createButton.addActionListener(e -> createCharacter());
        panel.add(createButton);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void createCharacter() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um nome para o personagem!", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (CharacterManager.characterNameExists(userId, name)) {
            JOptionPane.showMessageDialog(this, "Este nome de personagem já existe!", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Extrai raça e classe (remove emojis)
        String raceStr = ((String) raceBox.getSelectedItem()).split(" ")[0];
        String classStr = ((String) classBox.getSelectedItem()).split(" ")[0];

        try {
            Character character = new Character(name, raceStr, classStr);
            CharacterManager.addCharacter(userId, character);
            characters = CharacterManager.loadCharacters(userId);

            // Envia comando ao C++
            String cmd = "create_character " + raceStr + " " + classStr;
            if (processWriter != null) {
                processWriter.println(cmd);
                processWriter.flush();
            }

            nameField.setText("");
            refreshCharacterList();
            JOptionPane.showMessageDialog(this, "Personagem criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar personagem: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshCharacterList() {
        characterListPanel.removeAll();

        if (characters.isEmpty()) {
            JLabel emptyLabel = new JLabel("Nenhum personagem criado.");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            emptyLabel.setForeground(new Color(150, 150, 150));
            characterListPanel.add(emptyLabel);
        } else {
            for (Character ch : characters) {
                JPanel charPanel = createCharacterPanel(ch);
                characterListPanel.add(charPanel);
                characterListPanel.add(Box.createVerticalStrut(8));
            }
        }

        characterListPanel.add(Box.createVerticalGlue());
        characterListPanel.revalidate();
        characterListPanel.repaint();
    }

    private JPanel createCharacterPanel(Character ch) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(new Color(50, 50, 70));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 120), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Info
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(50, 50, 70));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("✦ " + ch.getName() + " [Lv. " + ch.getLevel() + "]");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(new Color(255, 200, 100));

        JLabel detailsLabel = new JLabel(getRaceIcon(ch.getRace()) + " " + ch.getRace() + "  |  " + getClassIcon(ch.getClazz()) + " " + ch.getClazz());
        detailsLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        detailsLabel.setForeground(new Color(180, 180, 200));

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(detailsLabel);

        panel.add(infoPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(50, 50, 70));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 6, 0));

        JButton playButton = new JButton("Entrar");
        playButton.setFont(new Font("Arial", Font.BOLD, 11));
        playButton.setBackground(new Color(100, 200, 100));
        playButton.setForeground(Color.WHITE);
        playButton.setFocusPainted(false);
        playButton.addActionListener(e -> enterGame(ch));

        JButton deleteButton = new JButton("Deletar");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 11));
        deleteButton.setBackground(new Color(200, 50, 50));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteCharacter(ch));

        buttonPanel.add(playButton);
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private void enterGame(Character ch) {
        try {
            service.GameLauncher.launchGame(userId, ch.getName());
            this.setVisible(false);
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao iniciar jogo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    } 
    private void deleteCharacter(Character ch) {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja deletar o personagem " + ch.getName() + "?", 
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                CharacterManager.deleteCharacter(userId, ch.getName());
                characters = CharacterManager.loadCharacters(userId);
                refreshCharacterList();
                JOptionPane.showMessageDialog(this, "Personagem deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao deletar personagem: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getRaceIcon(String race) {
        return switch (race) {
            case "Humano" -> "👤";
            case "Goblin" -> "👹";
            case "Elfo" -> "🧝";
            default -> "•";
        };
    }

    private String getClassIcon(String clazz) {
        return switch (clazz) {
            case "Caçador" -> "🏹";
            case "Guerreiro" -> "⚔️";
            case "Bruxo" -> "🔮";
            default -> "•";
        };
    }

    private void hookProcessStreams() {
        // writer to process stdin
        processWriter = new PrintWriter(new OutputStreamWriter(process.getOutputStream()), true);

        // Suppress stdout/stderr
        Thread outThread = new Thread(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while (br.readLine() != null) {
                    // Ignore
                }
            } catch (IOException ex) {
                // Ignore
            }
        }, "CppLobby-stdout-reader");
        outThread.setDaemon(true);
        outThread.start();

        Thread errThread = new Thread(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                while (br.readLine() != null) {
                    // Ignore
                }
            } catch (IOException ex) {
                // Ignore
            }
        }, "CppLobby-stderr-reader");
        errThread.setDaemon(true);
        errThread.start();

        Thread watch = new Thread(() -> {
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                // Ignore
            }
        }, "CppLobby-watcher");
        watch.setDaemon(true);
        watch.start();
    }
}
