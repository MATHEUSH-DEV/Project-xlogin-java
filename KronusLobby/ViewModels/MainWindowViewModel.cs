using ReactiveUI;

namespace KronusLobby.ViewModels;

public class MainWindowViewModel : ViewModelBase
{
    private string _charName = "MATUZATHEBOLD";
    public string CharacterName 
    {
        get => _charName;
        set => this.RaiseAndSetIfChanged(ref _charName, value);
    }

    public string ClassAndRace => "Humano Guerreiro | Level 1";
    public string PowerLevelDisplay => "POWER LEVEL: 58";
}