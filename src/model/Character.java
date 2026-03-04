package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um personagem do jogador com sistema de stats, level e habilidades.
 */
public class Character implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String race;
    private String clazz;
    private long createdAt;
    private int level;
    private int strength;  // STR
    private int agility;   // AGI
    private int intelligence; // INT
    private long experience;
    private int health;
    private int mana;
    private List<Ability> abilities = new ArrayList<>();

    public Character(String name, String race, String clazz) {
        this.name = name;
        this.race = race;
        this.clazz = clazz;
        this.createdAt = System.currentTimeMillis();
        this.level = 1;
        this.experience = 0;
        
        // Stats padrões baseados na classe
        initializeStats();
    }

    private void initializeStats() {
        // Stats padrões baseados na RAÇA
        switch (race) {
            case "Humano":
                this.strength = 18;
                this.agility = 12;
                this.intelligence = 10;
                break;
            case "Goblin":
                this.strength = 14;
                this.agility = 18;
                this.intelligence = 11;
                break;
            case "Elfo":
                this.strength = 16;
                this.agility = 14;
                this.intelligence = 16;
                break;
            default:
                this.strength = 18;
                this.agility = 12;
                this.intelligence = 10;
        }
        
        // Modificar baseado na CLASSE
        switch (clazz) {
            case "Caçador":
                this.agility += 4;
                this.strength -= 2;
                break;
            case "Guerreiro":
                this.strength += 3;
                this.intelligence -= 2;
                break;
            case "Bruxo":
                this.intelligence += 5;
                this.strength -= 4;
                break;
        }
        
        // Inicializar habilidades
        initializeAbilities();
        
        // Calcula saúde e mana
        this.health = 100 + (strength * 2);
        this.mana = 50 + (intelligence * 2);
    }
    
    /**
     * Inicializa as habilidades especiais baseado na classe do personagem.
     */
    private void initializeAbilities() {
        abilities.clear();
        
        switch (clazz) {
            case "Guerreiro":
                // Guerreiro: Foco em dano físico alto
                abilities.add(new Ability(
                    "Golpe Poderoso",
                    "Ataque devastador que causa 150% de dano",
                    20, 1.5, 3000
                ));
                abilities.add(new Ability(
                    "Fúria Berserker",
                    "Ataque selvagem que causa 200% de dano",
                    50, 2.0, 10000
                ));
                break;
                
            case "Caçador":
                // Caçador: Foco em precisão e múltiplos tiros
                abilities.add(new Ability(
                    "Tiro Preciso",
                    "Ataque preciso que causa 120% de dano",
                    15, 1.2, 2000
                ));
                abilities.add(new Ability(
                    "Chuva de Flechas",
                    "Múltiplos tiros causam 140% de dano",
                    30, 1.4, 5000
                ));
                break;
                
            case "Bruxo":
                // Bruxo: Foco em magia destrutiva
                abilities.add(new Ability(
                    "Bola de Fogo",
                    "Fogo mágico que causa 170% de dano",
                    25, 1.7, 2500
                ));
                abilities.add(new Ability(
                    "Maldição Sombria",
                    "Magia sombria que causa 190% de dano",
                    40, 1.9, 8000
                ));
                break;
        }
    }

    public String getName() { return name; }
    public String getRace() { return race; }
    public String getClazz() { return clazz; }
    public long getCreatedAt() { return createdAt; }
    public int getLevel() { return level; }
    public int getStrength() { return strength; }
    public int getAgility() { return agility; }
    public int getIntelligence() { return intelligence; }
    public long getExperience() { return experience; }
    public int getHealth() { return health; }
    public int getMana() { return mana; }
    
    /**
     * Retorna lista de habilidades do personagem.
     */
    public List<Ability> getAbilities() {
        return new ArrayList<>(abilities);
    }
    
    /**
     * Retorna uma habilidade específica pelo índice.
     */
    public Ability getAbility(int index) {
        if (index >= 0 && index < abilities.size()) {
            return abilities.get(index);
        }
        return null;
    }
    
    /**
     * Usa uma habilidade especial.
     * 
     * @param index Índice da habilidade (0 = primeira, 1 = segunda, etc)
     * @return A habilidade usada, ou null se não puder usar
     */
    public Ability useAbility(int index) {
        Ability ability = getAbility(index);
        if (ability != null && ability.canUse(this.mana)) {
            this.mana -= ability.getManaCost();
            ability.use();
            return ability;
        }
        return null;
    }

    public void addExperience(long exp) {
        this.experience += exp;
        checkLevelUp();
    }

    private void checkLevelUp() {
        long expRequired = (long) (1000 * Math.pow(1.1, level - 1));
        if (experience >= expRequired) {
            levelUp();
        }
    }

    public void levelUp() {
        this.level++;
        this.strength += 2;
        this.agility += 1;
        this.intelligence += 2;
        this.health = 100 + (strength * 2);
        this.mana = 50 + (intelligence * 2);
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health < 0) this.health = 0;
    }

    public void heal(int amount) {
        this.health += amount;
        if (this.health > 100 + (strength * 2)) {
            this.health = 100 + (strength * 2);
        }
    }

    /**
     * Restaura os stats do personagem do arquivo (JSON).
     * Usado ao carregar personagens salvos.
     */
    public void restoreStats(int level, int strength, int agility, int intelligence, int health, int mana, long experience) {
        this.level = level;
        this.strength = strength;
        this.agility = agility;
        this.intelligence = intelligence;
        this.health = health;
        this.mana = mana;
        this.experience = experience;
    }

    /**
     * Serializa o personagem para JSON (para enviar ao C++ game).
     */
    public String toJSON() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"name\": \"").append(escapeJson(name)).append("\",\n");
        json.append("  \"race\": \"").append(race).append("\",\n");
        json.append("  \"class\": \"").append(clazz).append("\",\n");
        json.append("  \"level\": ").append(level).append(",\n");
        json.append("  \"experience\": ").append(experience).append(",\n");
        json.append("  \"stats\": {\n");
        json.append("    \"strength\": ").append(strength).append(",\n");
        json.append("    \"agility\": ").append(agility).append(",\n");
        json.append("    \"intelligence\": ").append(intelligence).append("\n");
        json.append("  },\n");
        json.append("  \"health\": ").append(health).append(",\n");
        json.append("  \"mana\": ").append(mana).append(",\n");
        json.append("  \"abilities\": [\n");
        
        for (int i = 0; i < abilities.size(); i++) {
            Ability ab = abilities.get(i);
            json.append("    {\n");
            json.append("      \"name\": \"").append(escapeJson(ab.getName())).append("\",\n");
            json.append("      \"description\": \"").append(escapeJson(ab.getDescription())).append("\",\n");
            json.append("      \"manaCost\": ").append(ab.getManaCost()).append(",\n");
            json.append("      \"damageMultiplier\": ").append(ab.getDamageMultiplier()).append(",\n");
            json.append("      \"cooldownMs\": ").append(ab.getCooldownMs()).append("\n");
            json.append("    }");
            if (i < abilities.size() - 1) json.append(",");
            json.append("\n");
        }
        
        json.append("  ]\n");
        json.append("}\n");
        return json.toString();
    }

    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }

    @Override
    public String toString() {
        return String.format("Character{name='%s', race='%s', class='%s', level=%d, STR=%d, AGI=%d, INT=%d}", 
            name, race, clazz, level, strength, agility, intelligence);
    }
}
