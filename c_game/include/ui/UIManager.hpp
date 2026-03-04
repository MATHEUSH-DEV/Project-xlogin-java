#pragma once

#include "HUD.hpp"
#include <memory>

/**
 * Gerenciador de UI
 * Coordena HUD, menus, diálogos
 */
class UIManager {
public:
    UIManager();
    
    void update(const Player& player, const std::vector<std::unique_ptr<Enemy>>& enemies);
    void render(const Player& player);
    
    void toggleHUD() { hudVisible = !hudVisible; }
    void showPauseMenu();
    void showGameOverScreen(bool victory, int xpGained);
    
private:
    std::unique_ptr<HUD> hud;
    bool hudVisible = true;
    bool pauseMenuVisible = false;
};
