using Avalonia;
using System;
using KronusLobby.ViewModels;

namespace KronusLobby;

sealed class Program
{
    [STAThread]
    public static void Main(string[] args) 
    {
        // 1. Iniciamos com um ID padrão (caso abra o C# sozinho para testes)
        int userId = 1; 

        // 2. Se o Java enviou um ID, nós capturamos ele aqui [cite: 2026-02-13]
        if (args.Length > 0 && int.TryParse(args[0], out int idVindoDoJava)) 
        {
            userId = idVindoDoJava;
            Console.WriteLine($"[C#] ID recebido do Java: {userId}"); // Log para o seu teste de mesa
        }

        // 3. Configuração do App com o ID injetado
        BuildAvaloniaApp().StartWithClassicDesktopLifetime(args);
    }

    public static AppBuilder BuildAvaloniaApp()
        => AppBuilder.Configure<App>()
            .UsePlatformDetect()
            .WithInterFont()
            .LogToTrace();
}