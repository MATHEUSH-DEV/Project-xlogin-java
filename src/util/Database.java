package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.util.Properties;

/**
 * Classe para gerenciar conexões com banco de dados usando HikariCP.
 * Mantém um pool de conexões reutilizáveis para melhor performance.
 */
public class Database {
    private static HikariDataSource ds;

    static {
        try (InputStream in = Database.class.getResourceAsStream("/db.properties")) {
            Properties p = new Properties();
            p.load(in);

            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(p.getProperty("db.url"));
            cfg.setUsername(p.getProperty("db.user"));
            cfg.setPassword(p.getProperty("db.password"));
            cfg.setMaximumPoolSize(Integer.parseInt(p.getProperty("db.poolSize", "10")));
            cfg.setMinimumIdle(2);
            cfg.setConnectionTimeout(20000);
            cfg.setIdleTimeout(300000);
            cfg.setMaxLifetime(1800000);
            cfg.setAutoCommit(true);
            
            ds = new HikariDataSource(cfg);
            System.out.println("✓ ============================================");
            System.out.println("✓ Conexão com banco de dados MySQL estabelecida!");
            System.out.println("✓ Banco: kronus");
            System.out.println("✓ Host: localhost:3306");
            System.out.println("✓ Pool Size: " + cfg.getMaximumPoolSize());
            System.out.println("✓ ============================================");
        } catch (Exception e) {
            System.err.println("✗ ERRO ao conectar ao banco:");
            System.err.println("✗ Mensagem: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Obtém o DataSource (pool de conexões).
     * @return HikariDataSource com pool de conexões
     */
    public static HikariDataSource getDataSource() {
        if (ds == null) {
            throw new RuntimeException("DataSource não foi inicializado!");
        }
        return ds;
    }

    /**
     * Fecha o pool de conexões (chamar ao desligar a aplicação).
     */
    public static void close() {
        if (ds != null && !ds.isClosed()) {
            ds.close();
            System.out.println("✓ Pool de conexões fechado");
        }
    }

    /**
     * Verifica se está conectado ao banco.
     * @return true se conectado, false caso contrário
     */
    public static boolean isConnected() {
        try {
            return ds != null && !ds.isClosed();
        } catch (Exception e) {
            return false;
        }
    }
}
