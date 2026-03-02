package service;

import java.io.File;
import java.io.IOException;

public class GameLauncher {
    private static final String DOTNET_PATH = "dotnet";
    private static final String PROJECT_PATH = "./KronusLobby";

    /**
     * Inicia o processo do Lobby C# de forma assíncrona.
     * @param userId O ID do jogador vindo do banco de dados.
     */
    public static void launchLobby(int userId) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
            DOTNET_PATH,
            "run",
            "--project", PROJECT_PATH,
            String.valueOf(userId)
        );
        pb.directory(new File(System.getProperty("user.dir")));
        pb.inheritIO();
        pb.start();
        System.out.println("[Launcher] Lobby iniciado para o User: " + userId);
    }
}
