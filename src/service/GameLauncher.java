package service;

import java.io.File;
import java.io.IOException;
import model.Character;

public class GameLauncher {

    public static void launchLobby(String userId) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        String lobbyPath;
        if (os.contains("win")) {
            lobbyPath = "c_lobby\\lobby.exe";
        } else {
            lobbyPath = "./c_lobby/lobby";
        }

        File execFile = new File(System.getProperty("user.dir"), lobbyPath);
        if (!execFile.exists()) {
            throw new IOException(
                "Lobby executable not found: " + execFile.getAbsolutePath()
                + "\nBuild it with CMake or compile src/main.cpp with g++. See README or run: g++ -std=c++17 c_lobby\\src\\main.cpp -o c_lobby\\lobby.exe"
            );
        }

        ProcessBuilder pb = new ProcessBuilder(execFile.getAbsolutePath(), userId);
        pb.directory(new File(System.getProperty("user.dir")));
        Process process = pb.start();
        System.out.println("[Launcher] C++ Lobby iniciado para o User: " + userId + " usando: " + execFile.getAbsolutePath());
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            ui.CppLobbyWindow window = new ui.CppLobbyWindow(process, userId, execFile.getAbsolutePath());
            window.setVisible(true);
        });
    }

    public static void launchGame(String userId, String characterName) {
        Character character = util.CharacterManager.getCharacterByName(userId, characterName);
        if (character == null) {
            javax.swing.JOptionPane.showMessageDialog(null, "Personagem não encontrado!", "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        game.GameWindow gameWindow = new game.GameWindow(character, userId, null);
        gameWindow.setVisible(true);
        System.out.println("[GameLauncher] Jogo iniciado para: " + characterName);
    }
}
