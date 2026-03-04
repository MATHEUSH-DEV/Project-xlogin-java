#include "ui/HUD.hpp"
#include "ui/UIManager.hpp"
#include <raylib.h>
#include <sstream>
#include <iomanip>

HUD::HUD() {
    addLogMessage("=== Log do Jogo ===");
    addLogMessage("Bem-vindo a Floresta de Eldoria!");
    addLogMessage("Clique para se mover, 1-4 para usar habilidades");
}

void HUD::update(const Player& player, const std::vector<std::unique_ptr<Enemy>>& enemies) {
    // Update logic can go here
}

void HUD::render(const Player& player) {
    int screenWidth = GetScreenWidth();
    int screenHeight = GetScreenHeight();
    
    // === Left Panel: Stats ===
    DrawRectangle(0, 0, 300, screenHeight, {40, 40, 60, 200});
    
    // Title
    DrawText("📊 Estatísticas", 10, 10, 16, {255, 200, 100, 255});
    
    // Level
    std::stringstream ss;
    ss << "Level: " << player.level;
    DrawText(ss.str().c_str(), 10, 40, 14, {100, 200, 100, 255});
    
    // Health
    ss.str(""); ss.clear();
    ss << "❤️ HP: " << player.health << " / " << player.maxHealth;
    DrawText(ss.str().c_str(), 10, 70, 12, {200, 100, 100, 255});
    
    // Health Bar
    DrawRectangle(10, 90, 280, 20, {50, 50, 50, 255});
    int healthWidth = static_cast<int>(280.0f * player.health / player.maxHealth);
    DrawRectangle(10, 90, healthWidth, 20, {200, 50, 50, 255});
    
    // Mana
    ss.str(""); ss.clear();
    ss << "💙 Mana: " << player.mana << " / " << player.maxMana;
    DrawText(ss.str().c_str(), 10, 120, 12, {100, 150, 200, 255});
    
    // Mana Bar
    DrawRectangle(10, 140, 280, 20, {50, 50, 50, 255});
    int manaWidth = static_cast<int>(280.0f * player.mana / player.maxMana);
    DrawRectangle(10, 140, manaWidth, 20, {100, 150, 200, 255});
    
    // Stats
    ss.str(""); ss.clear();
    ss << "⚔️ STR: " << player.strength << " | 🏃 AGI: " << player.agility 
       << " | 🧠 INT: " << player.intelligence;
    DrawText(ss.str().c_str(), 10, 170, 10, {180, 180, 200, 255});
    
    // === Right Panel: Abilities ===
    DrawRectangle(screenWidth - 300, 0, 300, 300, {40, 40, 60, 200});
    DrawText("⚡ Habilidades", screenWidth - 290, 10, 14, {255, 150, 50, 255});
    
    int yPos = 40;
    for (size_t i = 0; i < player.abilities.size() && i < 4; ++i) {
        const auto& ability = player.abilities[i];
        
        // Ability button background
        Color bgColor = ability.canUse(player.mana) ? Color{100, 150, 200, 255} : Color{80, 80, 100, 255};
        DrawRectangle(screenWidth - 290, yPos, 280, 50, bgColor);
        
        // Ability name + hotkey
        ss.str(""); ss.clear();
        ss << "[" << ability.hotkey << "] " << ability.name;
        DrawText(ss.str().c_str(), screenWidth - 280, yPos + 5, 12, WHITE);
        
        // Mana cost
        ss.str(""); ss.clear();
        ss << "Mana: " << ability.manaCost;
        DrawText(ss.str().c_str(), screenWidth - 280, yPos + 20, 10, {150, 200, 255, 255});
        
        // Cooldown
        long cooldown = ability.getRemainingCooldown();
        if (cooldown > 0) {
            ss.str(""); ss.clear();
            ss << "CD: " << std::fixed << std::setprecision(1) << (cooldown / 1000.0f) << "s";
            DrawText(ss.str().c_str(), screenWidth - 280, yPos + 32, 10, {255, 100, 100, 255});
        }
        
        yPos += 60;
    }
    
    // === Bottom Panel: Combat Log ===
    DrawRectangle(0, screenHeight - 200, screenWidth, 200, {20, 20, 30, 200});
    DrawText("📜 Log de Combate", 10, screenHeight - 190, 12, {150, 255, 150, 255});
    
    renderCombatLog();
}

void HUD::addLogMessage(const std::string& message) {
    combatLog.push_back(message);
    if (combatLog.size() > MAX_LOG_LINES) {
        combatLog.pop_front();
    }
}

void HUD::clearLog() {
    combatLog.clear();
}

void HUD::renderStats(const Player& player) {
    // Implementation
}

void HUD::renderAbilities(const Player& player) {
    // Implementation
}

void HUD::renderCombatLog() {
    int screenHeight = GetScreenHeight();
    int yPos = screenHeight - 170;
    
    for (const auto& msg : combatLog) {
        DrawText(msg.c_str(), 20, yPos, 10, {150, 255, 150, 255});
        yPos += 18;
    }
}

void HUD::renderCooldowns(const Player& player) {
    // Implementation
}
