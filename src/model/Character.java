package model;

import java.io.Serializable;

/**
 * Representa um personagem do jogador com sistema de stats e level.
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
        // Stats padrões
        this.strength = 18;
        this.agility = 12;
        this.intelligence = 10;
        this.health = 100 + (strength * 2);
        this.mana = 50 + (intelligence * 2);
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

    @Override
    public String toString() {
        return String.format("Character{name='%s', race='%s', class='%s', level=%d, STR=%d, AGI=%d, INT=%d}", 
            name, race, clazz, level, strength, agility, intelligence);
    }
}
