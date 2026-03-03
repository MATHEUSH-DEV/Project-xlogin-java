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
 * Janela simples para mostrar a saída do processo C++ (lobby.exe)
 * e enviar entrada para ele.
 */
public class CppLobbyWindow extends JFrame {
    private final JTextArea outputArea = new JTextArea();
    private final JTextField inputField = new JTextField();
    private final Process process;
    private PrintWriter processWriter;

    public CppLobbyWindow(Process process, int userId, String execPath) {
        super("Kronus C++ Lobby (beta) - User: " + userId);
        this.process = process;
        initUI(userId, execPath);
        hookProcessStreams();
    }

    private void initUI(int userId, String execPath) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 420);
        setLocationRelativeTo(null);

        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(outputArea);
        getContentPane().setLayout(new BorderLayout(6, 6));

        JLabel header = new JLabel("[Launcher] C++ Lobby iniciado para o User: " + userId + " usando: " + execPath);
        header.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(4, 4));
        bottom.add(new JLabel("Digite comando:"), BorderLayout.WEST);
        bottom.add(inputField, BorderLayout.CENTER);

        getContentPane().add(bottom, BorderLayout.SOUTH);

        // enviar ao pressionar Enter
        inputField.addActionListener(e -> sendInput());

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

    private void appendLine(String line) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(line + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    private void hookProcessStreams() {
        // writer to process stdin
        processWriter = new PrintWriter(new OutputStreamWriter(process.getOutputStream()), true);

        // stdout reader
        Thread outThread = new Thread(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    appendLine(line);
                }
            } catch (IOException ex) {
                appendLine("[Error reading stdout] " + ex.getMessage());
            }
        }, "CppLobby-stdout-reader");
        outThread.setDaemon(true);
        outThread.start();

        // stderr reader
        Thread errThread = new Thread(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    appendLine(line);
                }
            } catch (IOException ex) {
                appendLine("[Error reading stderr] " + ex.getMessage());
            }
        }, "CppLobby-stderr-reader");
        errThread.setDaemon(true);
        errThread.start();

        // watcher thread para detectar fim do processo
        Thread watch = new Thread(() -> {
            try {
                int code = process.waitFor();
                appendLine("[Process exited] code=" + code);
                SwingUtilities.invokeLater(() -> inputField.setEditable(false));
            } catch (InterruptedException e) {
                appendLine("[Watcher interrupted]");
            }
        }, "CppLobby-watcher");
        watch.setDaemon(true);
        watch.start();
    }

    private void sendInput() {
        String text = inputField.getText();
        if (text == null) return;
        if (processWriter != null) {
            processWriter.println(text);
            processWriter.flush();
        }
        inputField.setText("");
    }
}
