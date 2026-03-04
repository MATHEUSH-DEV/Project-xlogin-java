#include "entities/Enemy.hpp"
#include <raylib.h>
#include <glm/glm.hpp>
#include <cmath>
#include <random>

Enemy::Enemy(Type t, int lvl, glm::vec2 spawnPos)
    : type(t), level(lvl), position(spawnPos), targetPosition(spawnPos) {
    initializeByType();
}

void Enemy::initializeByType() {
    switch (type) {
        case Type::GOBLIN:
            name = "Goblin";
            maxHealth = 50 + (level * 10);
            baseDamage = 15;
            moveSpeed = 80.0f;
            attackRange = 40.0f;
            xpReward = 100;
            break;
            
        case Type::WOLF:
            name = "Lobo";
            maxHealth = 60 + (level * 10);
            baseDamage = 18;
            moveSpeed = 120.0f;
            attackRange = 45.0f;
            xpReward = 120;
            break;
            
        case Type::FOREST_SPIRIT:
            name = "Espírito Florestal";
            maxHealth = 80 + (level * 15);
            baseDamage = 22;
            moveSpeed = 100.0f;
            attackRange = 60.0f;
            xpReward = 150;
            break;
            
        case Type::BANDIT:
            name = "Bandido";
            maxHealth = 70 + (level * 12);
            baseDamage = 20;
            moveSpeed = 110.0f;
            attackRange = 50.0f;
            xpReward = 130;
            break;
            
        case Type::BOSS:
            name = "Boss do Mundo";
            maxHealth = 200 + (level * 30);
            baseDamage = 35;
            moveSpeed = 90.0f;
            attackRange = 60.0f;
            xpReward = 500;
            break;
    }
    
    health = maxHealth;
}

void Enemy::update(float deltaTime, glm::vec2 playerPos) {
    if (isDead) return;
    
    // === Perseguir jogador ===
    targetPosition = playerPos;
    
    if (glm::distance(position, playerPos) > 10.0f) {
        glm::vec2 direction = glm::normalize(playerPos - position);
        position += direction * moveSpeed * deltaTime;
    }
    
    // === Cooldown de ataque ===
    if (attackCooldown > 0.0f) {
        attackCooldown -= deltaTime;
    }
}

void Enemy::render() {
    if (isDead) return;
    
    // Renderizar inimigo
    DrawRectangle(
        static_cast<int>(position.x - radius),
        static_cast<int>(position.y - radius),
        static_cast<int>(radius * 2),
        static_cast<int>(radius * 2),
        RED
    );
    
    // Health bar
    DrawRectangle(
        static_cast<int>(position.x - 25),
        static_cast<int>(position.y - 40),
        50, 5,
        DARKRED
    );
    int healthWidth = static_cast<int>(50.0f * health / maxHealth);
    DrawRectangle(
        static_cast<int>(position.x - 25),
        static_cast<int>(position.y - 40),
        healthWidth, 5,
        RED
    );
    
    // Nome
    DrawText(name.c_str(),
             static_cast<int>(position.x - 20),
             static_cast<int>(position.y + 30),
             10, WHITE);
}

void Enemy::takeDamage(int damage) {
    health -= damage;
    if (health <= 0) {
        isDead = true;
    }
}

int Enemy::attack() {
    if (canAttack() && !isDead) {
        attackCooldown = 2.0f;  // 2 segundo de cooldown
        return baseDamage;
    }
    return 0;
}
