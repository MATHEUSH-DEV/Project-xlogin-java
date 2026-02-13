using Microsoft.Data.Sqlite;

// Simulando o ID que viria do seu Launcher em Java (teteu = 4)
string userId = "4"; 
string connectionString = "Data Source=../kronus_local.db"; // Caminho para o seu banco atual

using (var connection = new SqliteConnection(connectionString))
{
    connection.Open();
    
    // Query para buscar o herói que acabamos de "semear" no banco
    var command = connection.CreateCommand();
    command.CommandText = "SELECT name, race, class, level FROM characters WHERE user_id = $id";
    command.Parameters.AddWithValue("$id", userId);

    using (var reader = command.ExecuteReader())
    {
        while (reader.Read())
        {
            var name = reader.GetString(0);
            var race = reader.GetString(1);
            var job = reader.GetString(2);
            
            Console.WriteLine($"--- KRONUS LOBBY ---");
            Console.WriteLine($"Welcome back, {name}!");
            Console.WriteLine($"Character: {race} {job} | Level: {reader.GetInt32(3)}");
        }
    }
}