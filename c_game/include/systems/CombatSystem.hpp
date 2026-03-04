#pragma once

#include "entities/Player.hpp"
#include "entities/Enemy.hpp"
#include <vector>
#include <memory>

/**
 * Sistema de combate
 * Fórmulas do Java: DanoAtaque = STR + (AGI/2)
 */
class CombatSystem {
public:
    struct CombatLog {
        std::string attacker;
        std::string target;
        int damage;
        std::string abilityUsed;
        long timestamp;
    };

public:
    static void update(float deltaTime, Player& player, std::vector<std::unique_ptr<Enemy>>& enemies);
    
    static int calculateDamage(const Player& attacker);
    static int calculateAbilityDamage(const Player& attacker, const Ability& ability);
    
    static void playerAttackEnemy(Player& player, Enemy& enemy);
    static void playerUseAbility(Player& player, Enemy& enemy, const Ability& ability);
    
    static void enemyAttackPlayer(Enemy& enemy, Player& player);
    
    static void spawnEnemies(int count, const glm::vec2& playerPos, 
                            std::vector<std::unique_ptr<Enemy>>& enemies);
    
    const std::vector<CombatLog>& getLogs() const { return logs; }
    
private:
    static std::vector<CombatLog> logs;
};
