using Avalonia;
using Avalonia.Controls.ApplicationLifetimes;
using Avalonia.Data.Core;
using Avalonia.Data.Core.Plugins;
using System.Linq;
using Avalonia.Markup.Xaml;
using KronusLobby.ViewModels;
using KronusLobby.Views;

namespace KronusLobby;

public partial class App : Application
{
    public override void Initialize()
    {
        AvaloniaXamlLoader.Load(this);
    }

    public override void OnFrameworkInitializationCompleted()
    {
        if (ApplicationLifetime is IClassicDesktopStyleApplicationLifetime desktop)
        {
            // 1. Pegamos o ID dos argumentos (enviados pelo Java)
            int userId = 1;
            if (desktop.Args.Length > 0 && int.TryParse(desktop.Args[0], out int id))
            {
                userId = id;
            }

            // 2. Criamos a ViewModel já passando o ID correto
            desktop.MainWindow = new MainWindow
            {
                DataContext = new MainWindowViewModel(userId),
            };
        }

        base.OnFrameworkInitializationCompleted();
    }

    private void DisableAvaloniaDataAnnotationValidation()
    {
        // Get an array of plugins to remove
        var dataValidationPluginsToRemove =
            BindingPlugins.DataValidators.OfType<DataAnnotationsValidationPlugin>().ToArray();

        // remove each entry found
        foreach (var plugin in dataValidationPluginsToRemove)
        {
            BindingPlugins.DataValidators.Remove(plugin);
        }
    }
}