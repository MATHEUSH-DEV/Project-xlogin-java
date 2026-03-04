#pragma once

#include <string>
#include <vector>
#include <glm/glm.hpp>
#include "Ability.hpp"

/**
 * Jogador - Equivalente ao Character.java do Java
 * Sincroniza com backend Java via JSON
 */
class Player {
public:
    // === STATS (do Character.java) ===
    std::string name;
    std::string race;
    std::string clazz;
    int level = 1;
    int experience = 0;
    
    // Base Stats
    int strength = 18;
    int agility = 12;
    int intelligence = 10;
    
    // Derived Stats
    int maxHealth = 100 + (strength * 2);
    int health = maxHealth;
    int maxMana = 50 + (intelligence * 2);
    int mana = maxMana;
    
    // === GAMEPLAY ===
    glm::vec2 position = {400.0f, 300.0f};
    glm::vec2 targetPosition = position;
    float moveSpeed = 150.0f;  // pixels/second
    float radius = 16.0f;       // collision radius
    
    // Animation
    int frameCounter = 0;
    int currentFrame = 0;
    
    // Abilities
    std::vector<Ability> abilities;
    
    // Combat
    bool isInCombat = false;
    int enemiesDefeated = 0;

public:
    Player(const std::string& n, const std::string& r, const std::string& c);
    
    void update(float deltaTime);
    void moveTo(glm::vec2 targetPos);
    void render();
    
    void addExperience(int exp);
    void levelUp();
    void takeDamage(int damage);
    void heal(int amount);
    void restoreMana(int amount);
    
    bool canUseAbility(int abilityIndex) const;
    Ability* useAbility(int abilityIndex);
    
    void loadFromJSON(const std::string& jsonData);
    std::string toJSON() const;
    
private:
    void initializeAbilities();
    void calculateStats();
};
