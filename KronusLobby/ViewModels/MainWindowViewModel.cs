using ReactiveUI;

namespace KronusLobby.ViewModels;

public class MainWindowViewModel : ViewModelBase
{
    // Iniciamos com string.Empty para evitar o Warning CS8618 [cite: 2026-02-13]
    private string _charName = string.Empty;
    private string? _powerLevelDisplay;

    // O construtor agora recebe o ID vindo do Java via App.axaml.cs [cite: 2026-02-13]
    public MainWindowViewModel(int userId)
    {
        // Lógica de simulação de busca (Mock) [cite: 2026-02-13]
        if (userId == 4) 
        {
            CharacterName = "MATUZA THE BOLD";
            PowerLevelDisplay = "POWER LEVEL: 99 (ADMIN)";
        }
        else 
        {
            CharacterName = $"HERÓI ID: {userId}";
            PowerLevelDisplay = "POWER LEVEL: 10";
        }
    }

    public string CharacterName 
    {
        get => _charName;
        // RaiseAndSetIfChanged avisa a View (XAML) para atualizar o texto na tela [cite: 2026-02-13]
        set => this.RaiseAndSetIfChanged(ref _charName, value);
    }

    public string? PowerLevelDisplay 
    { 
        get => _powerLevelDisplay; 
        set => this.RaiseAndSetIfChanged(ref _powerLevelDisplay, value); 
    }

    // Propriedade extra para facilitar o design do Lobby [cite: 2026-02-05]
    public string ClassAndRace => "Humano Guerreiro | Level 1";
}