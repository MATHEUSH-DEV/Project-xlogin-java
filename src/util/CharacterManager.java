package util;

import model.Character;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia persistência de personagens em banco de dados SQLite.
 * Banco de dados portável sem necessidade de servidor.
 * Arquivo: kronus_data/characters.db
 */
public class CharacterManager {

    /**
     * MÉTODO 1: Carrega todos os personagens de um usuário do banco.
     * @param userId ID do usuário
     * @return Lista de personagens
     */
    public static List<Character> loadCharacters(int userId) {
        List<Character> characters = new ArrayList<>();
        String sql = "SELECT name, race, clazz, level, strength, agility, intelligence, health, mana, experience FROM characters WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Criar personagem com dados básicos
                    Character ch = new Character(
                        rs.getString("name"),
                        rs.getString("race"),
                        rs.getString("clazz")
                    );
                    
                    // Restaurar stats do banco de dados
                    ch.restoreStats(
                        rs.getInt("level"),
                        rs.getInt("strength"),
                        rs.getInt("agility"),
                        rs.getInt("intelligence"),
                        rs.getInt("health"),
                        rs.getInt("mana"),
                        rs.getLong("experience")
                    );
                    
                    characters.add(ch);
                }
            }
            
            System.out.println("✓ Carregados " + characters.size() + " personagens do usuário " + userId);
            
        } catch (SQLException e) {
            System.err.println("✗ Erro ao carregar personagens: " + e.getMessage());
            e.printStackTrace();
        }
        
        return characters;
    }

    /**
     * MÉTODO 2: Adiciona um novo personagem ao banco.
     * @param userId ID do usuário
     * @param ch Personagem a ser adicionado
     * @throws IOException Se ocorrer erro (nome duplicado, etc)
     */
    public static void addCharacter(int userId, Character ch) throws IOException {
        String sql = "INSERT INTO characters (user_id, name, race, clazz, level, strength, agility, intelligence, health, mana, experience) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        
        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, ch.getName());
            ps.setString(3, ch.getRace());
            ps.setString(4, ch.getClazz());
            ps.setInt(5, ch.getLevel());
            ps.setInt(6, ch.getStrength());
            ps.setInt(7, ch.getAgility());
            ps.setInt(8, ch.getIntelligence());
            ps.setInt(9, ch.getHealth());
            ps.setInt(10, ch.getMana());
            ps.setLong(11, ch.getExperience());
            
            int inserted = ps.executeUpdate();
            if (inserted > 0) {
                System.out.println("✓ Personagem '" + ch.getName() + "' criado com sucesso!");
            }
            
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                throw new IOException("✗ Este nome de personagem já existe!");
            }
            throw new IOException("✗ Erro ao criar personagem: " + e.getMessage());
        }
    }

    /**
     * MÉTODO 3: Atualiza um personagem no banco (salva progresso).
     * Chamado quando sai do jogo para guardar level, exp, hp, mana, etc.
     * @param userId ID do usuário
     * @param ch Personagem a ser atualizado
     * @throws IOException Se ocorrer erro
     */
    public static void updateCharacter(int userId, Character ch) throws IOException {
        String sql = "UPDATE characters SET level=?, strength=?, agility=?, intelligence=?, health=?, mana=?, experience=? WHERE user_id=? AND name=?";
        
        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, ch.getLevel());
            ps.setInt(2, ch.getStrength());
            ps.setInt(3, ch.getAgility());
            ps.setInt(4, ch.getIntelligence());
            ps.setInt(5, ch.getHealth());
            ps.setInt(6, ch.getMana());
            ps.setLong(7, ch.getExperience());
            ps.setInt(8, userId);
            ps.setString(9, ch.getName());
            
            int updated = ps.executeUpdate();
            if (updated > 0) {
                System.out.println("✓ Personagem '" + ch.getName() + "' atualizado! Level: " + ch.getLevel() + " | XP: " + ch.getExperience());
            }
            
        } catch (SQLException e) {
            throw new IOException("✗ Erro ao atualizar personagem: " + e.getMessage());
        }
    }

    /**
     * MÉTODO 4: Deleta um personagem do banco.
     * @param userId ID do usuário
     * @param name Nome do personagem
     * @throws IOException Se ocorrer erro
     */
    public static void deleteCharacter(int userId, String name) throws IOException {
        String sql = "DELETE FROM characters WHERE user_id=? AND name=?";
        
        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, name);
            
            int deleted = ps.executeUpdate();
            if (deleted > 0) {
                System.out.println("✓ Personagem '" + name + "' deletado com sucesso!");
            }
            
        } catch (SQLException e) {
            throw new IOException("✗ Erro ao deletar personagem: " + e.getMessage());
        }
    }

    /**
     * MÉTODO 5: Verifica se já existe um personagem com esse nome.
     * Chamado antes de criar novo personagem.
     * @param userId ID do usuário
     * @param name Nome a verificar
     * @return true se existe, false caso contrário
     */
    public static boolean characterNameExists(int userId, String name) {
        String sql = "SELECT 1 FROM characters WHERE user_id=? AND name=? LIMIT 1";
        
        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, name);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao verificar nome: " + e.getMessage());
            return false;
        }
    }

    /**
     * MÉTODO 6: Salva personagens (compatibilidade com código anterior).
     * Agora apenas chama updateCharacter para cada personagem.
     * @param userId ID do usuário
     * @param characters Lista de personagens
     * @throws IOException Se ocorrer erro
     */
    public static void saveCharacters(int userId, List<Character> characters) throws IOException {
        for (Character ch : characters) {
            updateCharacter(userId, ch);
        }
    }

    /**
     * MÉTODO 7: Obtém um personagem específico pelo nome.
     * @param userId ID do usuário
     * @param name Nome do personagem
     * @return Character ou null se não encontrado
     */
    public static Character getCharacterByName(int userId, String name) {
        String sql = "SELECT id, name, race, clazz, level, strength, agility, intelligence, health, mana, experience FROM characters WHERE user_id=? AND name=? LIMIT 1";
        
        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, name);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Character ch = new Character(
                        rs.getString("name"),
                        rs.getString("race"),
                        rs.getString("clazz")
                    );
                    
                    ch.restoreStats(
                        rs.getInt("level"),
                        rs.getInt("strength"),
                        rs.getInt("agility"),
                        rs.getInt("intelligence"),
                        rs.getInt("health"),
                        rs.getInt("mana"),
                        rs.getLong("experience")
                    );
                    
                    return ch;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao carregar personagem: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * MÉTODO 8: Conta quantos personagens um usuário tem.
     * @param userId ID do usuário
     * @return Número de personagens
     */
    public static int countCharacters(int userId) {
        String sql = "SELECT COUNT(*) FROM characters WHERE user_id=?";
        
        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao contar personagens: " + e.getMessage());
        }
        
        return 0;
    }
}
