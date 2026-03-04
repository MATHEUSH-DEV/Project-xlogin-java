#include "systems/CombatSystem.hpp"
#include <random>
#include <algorithm>
#include <chrono>

std::vector<CombatSystem::CombatLog> CombatSystem::logs;

void CombatSystem::update(float deltaTime, Player& player, std::vector<std::unique_ptr<Enemy>>& enemies) {
    // Remover inimigos mortos
    enemies.erase(
        std::remove_if(enemies.begin(), enemies.end(),
                      [&player](const std::unique_ptr<Enemy>& e) {
                          return e->isDead;
                      }),
        enemies.end()
    );
    
    // Ataque de inimigos no jogador
    for (auto& enemy : enemies) {
        if (glm::distance(player.position, enemy->position) < enemy->attackRange) {
            if (enemy->canAttack()) {
                int damage = enemy->attack();
                player.takeDamage(damage);
            }
        }
    }
}

int CombatSystem::calculateDamage(const Player& attacker) {
    // Fórmula do Java: DanoAtaque = STR + (AGI/2)
    return attacker.strength + (attacker.agility / 2);
}

int CombatSystem::calculateAbilityDamage(const Player& attacker, const Ability& ability) {
    int baseDamage = calculateDamage(attacker);
    return ability.calculateDamage(baseDamage);
}

void CombatSystem::playerAttackEnemy(Player& player, Enemy& enemy) {
    int damage = calculateDamage(player);
    enemy.takeDamage(damage);
    
    CombatLog log{
        player.name,
        enemy.name,
        damage,
        "Ataque Básico",
        std::chrono::system_clock::now().time_since_epoch().count()
    };
    logs.push_back(log);
}

void CombatSystem::playerUseAbility(Player& player, Enemy& enemy, const Ability& ability) {
    int damage = calculateAbilityDamage(player, ability);
    enemy.takeDamage(damage);
    player.mana -= ability.manaCost;
    
    CombatLog log{
        player.name,
        enemy.name,
        damage,
        ability.name,
        std::chrono::system_clock::now().time_since_epoch().count()
    };
    logs.push_back(log);
}

void CombatSystem::enemyAttackPlayer(Enemy& enemy, Player& player) {
    int damage = enemy.attack();
    player.takeDamage(damage);
    
    CombatLog log{
        enemy.name,
        player.name,
        damage,
        "Ataque",
        std::chrono::system_clock::now().time_since_epoch().count()
    };
    logs.push_back(log);
}

void CombatSystem::spawnEnemies(int count, const glm::vec2& playerPos,
                                 std::vector<std::unique_ptr<Enemy>>& enemies) {
    static std::random_device rd;
    static std::mt19937 gen(rd());
    std::uniform_real_distribution<> distX(-300, 300);
    std::uniform_real_distribution<> distY(-300, 300);
    std::uniform_int_distribution<> typeRoll(0, 3);
    
    Enemy::Type types[] = {Enemy::Type::GOBLIN, Enemy::Type::WOLF,
                           Enemy::Type::FOREST_SPIRIT, Enemy::Type::BANDIT};
    
    for (int i = 0; i < count; ++i) {
        glm::vec2 spawnPos = playerPos + glm::vec2(
            static_cast<float>(distX(gen)),
            static_cast<float>(distY(gen))
        );
        
        auto enemy = std::make_unique<Enemy>(types[typeRoll(gen)], 1, spawnPos);
        enemies.push_back(std::move(enemy));
    }
}
