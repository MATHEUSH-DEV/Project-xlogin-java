package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Janela de Lobby do Kronus Rift estilo WoW.
 * Interface simples para criação de personagem (Raça + Classe).
 */
public class CppLobbyWindow extends JFrame {
    private final JTextArea characterListArea = new JTextArea();
    private final Process process;
    private PrintWriter processWriter;

    public CppLobbyWindow(Process process, int userId, String execPath) {
        super("Kronus Rift - Lobby");
        this.process = process;
        initUI(userId, execPath);
        hookProcessStreams();
    }

    private void initUI(int userId, String execPath) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        getContentPane().setLayout(new BorderLayout(12, 12));
        getContentPane().setBackground(new Color(20, 20, 30));

        // --- HEADER ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(40, 40, 60));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titleLabel = new JLabel("Kronus Rift");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 200, 100));

        JLabel subtitleLabel = new JLabel("Bem-vindo ao Lobby - Criação de Personagem");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(150, 150, 150));

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        getContentPane().add(headerPanel, BorderLayout.NORTH);

        // --- CENTER: Character List ---
        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        centerPanel.setBackground(new Color(20, 20, 30));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel charactersLabel = new JLabel("Seus Personagens:");
        charactersLabel.setFont(new Font("Arial", Font.BOLD, 16));
        charactersLabel.setForeground(new Color(200, 200, 200));

        characterListArea.setEditable(false);
        characterListArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        characterListArea.setBackground(new Color(30, 30, 40));
        characterListArea.setForeground(new Color(200, 200, 200));
        characterListArea.setText("Nenhum personagem criado ainda.\nUse os controles abaixo para criar um novo personagem.");

        JScrollPane scrollArea = new JScrollPane(characterListArea);
        scrollArea.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 120), 1));

        centerPanel.add(charactersLabel, BorderLayout.NORTH);
        centerPanel.add(scrollArea, BorderLayout.CENTER);

        getContentPane().add(centerPanel, BorderLayout.CENTER);

        // --- BOTTOM: Character Creation Controls ---
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(40, 40, 60));
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 12));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Race selection
        JLabel raceLabel = new JLabel("Raça:");
        raceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        raceLabel.setForeground(new Color(200, 200, 200));
        JComboBox<String> raceBox = new JComboBox<>(new String[]{"Humano", "Goblin", "Elfo"});
        raceBox.setFont(new Font("Arial", Font.PLAIN, 12));
        raceBox.setPreferredSize(new Dimension(120, 32));

        // Class selection
        JLabel classLabel = new JLabel("Classe:");
        classLabel.setFont(new Font("Arial", Font.BOLD, 14));
        classLabel.setForeground(new Color(200, 200, 200));
        JComboBox<String> classBox = new JComboBox<>(new String[]{"Caçador", "Guerreiro", "Bruxo"});
        classBox.setFont(new Font("Arial", Font.PLAIN, 12));
        classBox.setPreferredSize(new Dimension(120, 32));

        // Create button
        JButton createButton = new JButton("Criar Personagem");
        createButton.setFont(new Font("Arial", Font.BOLD, 14));
        createButton.setPreferredSize(new Dimension(180, 40));
        createButton.setBackground(new Color(200, 100, 50));
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        createButton.addActionListener(e -> {
            String race = (String) raceBox.getSelectedItem();
            String clazz = (String) classBox.getSelectedItem();
            String cmd = "create_character " + race + " " + clazz;
            addCharacterToList(race, clazz);
            if (processWriter != null) {
                processWriter.println(cmd);
                processWriter.flush();
            }
        });

        controlPanel.add(raceLabel);
        controlPanel.add(raceBox);
        controlPanel.add(classLabel);
        controlPanel.add(classBox);
        controlPanel.add(createButton);

        getContentPane().add(controlPanel, BorderLayout.SOUTH);

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
        });
    }

    private void addCharacterToList(String race, String clazz) {
        SwingUtilities.invokeLater(() -> {
            String text = characterListArea.getText();
            if (text.contains("Nenhum personagem criado ainda")) {
                characterListArea.setText("");
            }
            characterListArea.append("✦ " + race + " - " + clazz + "\n");
        });
    }

    private void hookProcessStreams() {
        // writer to process stdin
        processWriter = new PrintWriter(new OutputStreamWriter(process.getOutputStream()), true);

        // stdout reader (suppress standard output)
        Thread outThread = new Thread(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while (br.readLine() != null) {
                    // Ignore stdout from C++ process
                }
            } catch (IOException ex) {
                // Ignore
            }
        }, "CppLobby-stdout-reader");
        outThread.setDaemon(true);
        outThread.start();

        // stderr reader (suppress standard error)
        Thread errThread = new Thread(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                while (br.readLine() != null) {
                    // Ignore stderr from C++ process
                }
            } catch (IOException ex) {
                // Ignore
            }
        }, "CppLobby-stderr-reader");
        errThread.setDaemon(true);
        errThread.start();

        // watcher thread para detectar fim do processo
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
