package old;

import java.io.File;
import java.io.IOException;

public class GameLauncher {
    private static final String DOTNET_PATH = "dotnet"; // Já configuramos no seu Linux
    private static final String PROJECT_PATH = "./KronusLobby";

    /**
     * Inicia o processo do Lobby C# de forma assíncrona.
     * 
     * @param userId O ID do jogador vindo do banco de dados.
     */
    public static void launchLobby(int userId) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                DOTNET_PATH,
                "run",
                "--project", PROJECT_PATH,
                String.valueOf(userId));

        // Define o diretório de trabalho para a raiz do projetos
        pb.directory(new File(System.getProperty("user.dir")));

        // Redireciona erros e saídas para o terminal atual (essencial no Linux)
        pb.inheritIO();

        pb.start();
        System.out.println("[Launcher] Lobby iniciado para o User: " + userId);
    }
}