using Microsoft.Data.Sqlite;
using KronusLobby;

// Simulando o ID que viria do seu Launcher em Java (teteu = 4)
string userId = "4";
string connectionString = "Data Source=../kronus_local.db"; // Caminho para o seu banco atual

using (var connection = new SqliteConnection(connectionString))
{
    connection.Open();

    // Query para buscar o herói que acabamos de "semear" no banco
    var command = connection.CreateCommand();
    command.CommandText = "SELECT name, race, class, level, strength, agility, intelligence FROM characters WHERE user_id = $id";
    command.Parameters.AddWithValue("$id", userId);

    using (var reader = command.ExecuteReader())
    {
        while (reader.Read())
        {

            var hero = new Character
            {
                Name = reader.GetString(0),
                Race = reader.GetString(1),
                Class = reader.GetString(2),
                Level = reader.GetInt32(3),
                Strength = reader.GetInt32(4),     // Agora o índice 4 existe!
                Agility = reader.GetInt32(5),      // Índice 5
                Intelligence = reader.GetInt32(6)  // Índice 6
            };

            hero.DisplaySheet();

        

            var name = reader.GetString(0);
            var race = reader.GetString(1);
            var job = reader.GetString(2);

            Console.WriteLine($"--- KRONUS LOBBY ---");
            Console.WriteLine($"Welcome back, {name}!");
            Console.WriteLine($"Character: {race} {job} | Level: {reader.GetInt32(3)}");
        }
    }
}