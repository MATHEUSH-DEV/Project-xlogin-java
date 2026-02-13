using System;
namespace KronusLobby;

public class Character
{
    // Identificadores
    public string Name { get; set; } = string.Empty;
    public string Race { get; set; } = string.Empty;
    public string Class { get; set; } = string.Empty;
    
    // Status Evolutivos
    public int Level { get; set; }
    
    // Atributos Base (Os mesmos que criamos no DBeaver!)
    public int Strength { get; set; }
    public int Agility { get; set; }
    public int Intelligence { get; set; }

    // Este método é o "espaço reservado" para a sua futura DLL em C++
    public int CalculatePowerLevel()
    {
        // No futuro, chamaremos a DLL: return NativeDLL.GetPower(Strength, Agility);
        // Por enquanto, usamos uma lógica de C# pura para testes
        return (Strength * 2) + (Agility * 1) + (Intelligence * 1);
    }

    public void DisplaySheet()
    {
        Console.WriteLine("\n==============================");
        Console.WriteLine($"   NAME: {Name.ToUpper()}");
        Console.WriteLine($"   JOB:  {Race} {Class}");
        Console.WriteLine($"   LVL:  {Level}");
        Console.WriteLine("------------------------------");
        Console.WriteLine($"   STR: {Strength} | AGI: {Agility} | INT: {Intelligence}");
        Console.WriteLine($"   POWER LEVEL: {CalculatePowerLevel()}");
        Console.WriteLine("==============================\n");
    }
}