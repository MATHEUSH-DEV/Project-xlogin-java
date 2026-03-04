#include "ui/UIManager.hpp"

UIManager::UIManager()
    : hud(std::make_unique<HUD>()) {
}

void UIManager::update(const Player& player) {
    if (hudVisible && hud) {
        hud->update(player);
    }
}

void UIManager::render(const Player& player) {
    if (hudVisible && hud) {
        hud->render(player);
    }
    
    if (pauseMenuVisible) {
        // TODO: Render pause menu
    }
}

void UIManager::showPauseMenu() {
    pauseMenuVisible = true;
}

void UIManager::showGameOverScreen(bool victory, int xpGained) {
    // TODO: Render game over screen
}
