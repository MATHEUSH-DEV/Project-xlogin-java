#pragma once

#include <glm/glm.hpp>
#include <string>
#include <vector>

/**
 * Inimigo - Spawned durante combate
 * Similar ao sistema de GameWorld.java
 */
class Enemy {
public:
    enum class Type {
        GOBLIN,
        WOLF,
        FOREST_SPIRIT,
        BANDIT,
        BOSS
    };

    std::string name;
    Type type;
    
    int level = 1;
    int maxHealth = 50;
    int health = maxHealth;
    int baseDamage = 15;
    
    glm::vec2 position;
    glm::vec2 targetPosition;
    float moveSpeed = 80.0f;
    float radius = 16.0f;
    
    float attackCooldown = 0.0f;
    float attackRange = 50.0f;
    
    int xpReward = 100;
    bool isDead = false;

public:
    Enemy(Type t, int lvl, glm::vec2 spawnPos);
    
    void update(float deltaTime, glm::vec2 playerPos);
    void render();
    
    void takeDamage(int damage);
    bool isAlive() const { return health > 0; }
    bool canAttack() const { return attackCooldown <= 0.0f; }
    int attack();
    
private:
    void initializeByType();
};
