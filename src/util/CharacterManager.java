package util;

import model.Character;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia persistência de personagens em arquivo JSON simples.
 */
public class CharacterManager {
    private static final String CHARACTERS_DIR = "characters";

    static {
        new File(CHARACTERS_DIR).mkdirs();
    }

    /**
     * Salva os personagens de um usuário em JSON.
     */
    public static void saveCharacters(int userId, List<Character> characters) throws IOException {
        File file = getCharacterFile(userId);
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < characters.size(); i++) {
            Character ch = characters.get(i);
            json.append("  {\n");
            json.append("    \"name\": \"").append(ch.getName()).append("\",\n");
            json.append("    \"race\": \"").append(ch.getRace()).append("\",\n");
            json.append("    \"clazz\": \"").append(ch.getClazz()).append("\",\n");
            json.append("    \"createdAt\": ").append(ch.getCreatedAt()).append("\n");
            json.append("  }");
            if (i < characters.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("]");
        Files.write(file.toPath(), json.toString().getBytes());
    }

    /**
     * Carrega os personagens de um usuário do JSON.
     */
    public static List<Character> loadCharacters(int userId) {
        File file = getCharacterFile(userId);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        List<Character> characters = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            content = content.trim();
            if (content.startsWith("[") && content.endsWith("]")) {
                content = content.substring(1, content.length() - 1);
                String[] blocks = content.split("\\},\\s*\\{");
                for (String block : blocks) {
                    block = block.replace("{", "").replace("}", "").trim();
                    if (block.isEmpty()) continue;
                    
                    String name = extractJsonValue(block, "name");
                    String race = extractJsonValue(block, "race");
                    String clazz = extractJsonValue(block, "clazz");
                    
                    if (!name.isEmpty() && !race.isEmpty() && !clazz.isEmpty()) {
                        characters.add(new Character(name, race, clazz));
                    }
                }
            }
        } catch (IOException e) {
            // Retornar lista vazia em caso de erro
        }
        return characters;
    }

    /**
     * Verifica se um nome de personagem já existe para o usuário.
     */
    public static boolean characterNameExists(int userId, String characterName) {
        List<Character> characters = loadCharacters(userId);
        return characters.stream().anyMatch(c -> c.getName().equalsIgnoreCase(characterName));
    }

    /**
     * Adiciona um personagem à lista e salva em JSON.
     */
    public static void addCharacter(int userId, Character character) throws IOException {
        List<Character> characters = loadCharacters(userId);
        characters.add(character);
        saveCharacters(userId, characters);
    }

    /**
     * Remove um personagem da lista e salva em JSON.
     */
    public static void deleteCharacter(int userId, String characterName) throws IOException {
        List<Character> characters = loadCharacters(userId);
        characters.removeIf(c -> c.getName().equalsIgnoreCase(characterName));
        saveCharacters(userId, characters);
    }

    private static File getCharacterFile(int userId) {
        return new File(CHARACTERS_DIR, "user_" + userId + "_characters.json");
    }

    private static String extractJsonValue(String json, String key) {
        int startIdx = json.indexOf("\"" + key + "\"");
        if (startIdx == -1) return "";
        startIdx = json.indexOf("\"", startIdx + key.length() + 3) + 1;
        int endIdx = json.indexOf("\"", startIdx);
        return endIdx > startIdx ? json.substring(startIdx, endIdx) : "";
    }
}
