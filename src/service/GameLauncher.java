package service;

import java.io.File;
import java.io.IOException;

/**
 * Substitui o launcher original do .NET/Avalonia por um launcher que inicia
 * um binário C++ localizado em `c_lobby/`.
 * O binário recebido é executável simples que aceita o userId como argumento.
 */
public class GameLauncher {

    /**
     * Inicia o processo do Lobby C++ de forma assíncrona.
     * Procura por `c_lobby/lobby` (unix) ou `c_lobby\\lobby.exe` (windows).
     * @param userId O ID do jogador vindo do banco de dados.
     */
    public static void launchLobby(int userId) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        String lobbyPath;
        if (os.contains("win")) {
            lobbyPath = "c_lobby\\lobby.exe";
        } else {
            lobbyPath = "./c_lobby/lobby";
        }

        // Verifica se o executável existe antes de tentar executar.
        File execFile = new File(System.getProperty("user.dir"), lobbyPath);
        if (!execFile.exists()) {
            throw new IOException(
                "Lobby executable not found: " + execFile.getAbsolutePath()
                + "\nBuild it with CMake or compile src/main.cpp with g++. See README or run: g++ -std=c++17 c_lobby\\src\\main.cpp -o c_lobby\\lobby.exe"
            );
        }

        ProcessBuilder pb = new ProcessBuilder(execFile.getAbsolutePath(), String.valueOf(userId));
        pb.directory(new File(System.getProperty("user.dir")));
        pb.inheritIO();
        pb.start();
        System.out.println("[Launcher] C++ Lobby iniciado para o User: " + userId + " usando: " + execFile.getAbsolutePath());
    }
}
