package util;

import java.sql.*;

/**
 * Gerenciador de conexão SQLite.
 * Banco de dados portável que funciona sem servidor.
 * Arquivo fica em: kronus_data/characters.db
 */
public class DatabaseSQLite {
    private static Connection connection;

    static {
        try {
            // Carrega driver SQLite
            Class.forName("org.sqlite.JDBC");
            
            // Cria/conecta ao banco (arquivo local)
            String dbPath = "kronus_data/characters.db";
            String url = "jdbc:sqlite:" + dbPath;
            
            // Cria conexão com propriedades importantes
            java.util.Properties props = new java.util.Properties();
            props.setProperty("journal_mode", "WAL");  // Write-Ahead Logging
            
            connection = DriverManager.getConnection(url, props);
            
            // Configurações para melhor performance e confiabilidade
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
                stmt.execute("PRAGMA synchronous = NORMAL");
                stmt.execute("PRAGMA cache_size = 10000");
            }
            
            System.out.println("✓ ============================================");
            System.out.println("✓ Conectado ao SQLite: " + dbPath);
            System.out.println("✓ ============================================");
            
            // Criar tabelas se não existirem
            initTables();
        } catch (Exception e) {
            System.err.println("✗ Erro ao conectar SQLite: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Cria tabelas se não existirem.
     */
    private static void initTables() throws SQLException {
        String createCharacters = "CREATE TABLE IF NOT EXISTS characters (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "user_id INTEGER NOT NULL," +
            "name TEXT NOT NULL," +
            "race TEXT NOT NULL," +
            "clazz TEXT NOT NULL," +
            "level INTEGER DEFAULT 1," +
            "strength INTEGER DEFAULT 18," +
            "agility INTEGER DEFAULT 12," +
            "intelligence INTEGER DEFAULT 10," +
            "health INTEGER DEFAULT 100," +
            "mana INTEGER DEFAULT 50," +
            "experience INTEGER DEFAULT 0," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "UNIQUE(user_id, name)" +
            ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createCharacters);
            System.out.println("✓ Tabela 'characters' verificada/criada");
        }
    }

    /**
     * Obtém conexão com banco de dados (reutiliza a mesma conexão).
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Conexão SQLite não foi inicializada corretamente");
        }
        return connection;
    }

    /**
     * Fecha conexão (chamar ao desligar aplicação).
     */
    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Conexão SQLite fechada");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}
