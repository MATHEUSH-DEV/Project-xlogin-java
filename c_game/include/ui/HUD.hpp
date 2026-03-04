#pragma once

#include "entities/Player.hpp"
#include "entities/Enemy.hpp"
#include <vector>
#include <string>
#include <deque>

/**
 * HUD - Head-Up Display
 * Renderiza stats do player, abilities, cooldowns, combate log
 */
class HUD {
public:
    HUD();
    
    void update(const Player& player, const std::vector<std::unique_ptr<Enemy>>& enemies);
    void render(const Player& player);
    
    void addLogMessage(const std::string& message);
    void clearLog();
    
private:
    std::deque<std::string> combatLog;
    static constexpr int MAX_LOG_LINES = 20;
    
    void renderStats(const Player& player);
    void renderAbilities(const Player& player);
    void renderCombatLog();
    void renderCooldowns(const Player& player);
};
