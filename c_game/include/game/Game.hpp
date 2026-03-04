#pragma once

#include "GameWorld.hpp"
#include "systems/InputHandler.hpp"
#include "systems/NetworkClient.hpp"
#include "ui/UIManager.hpp"
#include <memory>

/**
 * GAME ENGINE PRINCIPAL
 * Orquestra todo o loop do jogo
 * 
 * - 60 FPS target
 * - Sincroniza com Java backend
 * - Gerencia renderização e input
 */
class Game {
public:
    Game(const std::string& gameTitle, int width, int height);
    ~Game();
    
    bool initialize(const std::string& playerJson);
    void run();
    void shutdown();
    
    bool isRunning() const { return running; }
    
private:
    // === WINDOW ===
    std::string title;
    int screenWidth, screenHeight;
    bool running = false;
    float targetFPS = 60.0f;
    
    // === GAME SYSTEMS ===
    std::unique_ptr<GameWorld> world;
    std::unique_ptr<InputHandler> inputHandler;
    std::unique_ptr<NetworkClient> networkClient;
    std::unique_ptr<UIManager> uiManager;
    
    // === TIMING ===
    float deltaTime = 0.0f;
    float totalElapsedTime = 0.0f;
    
    // === CALLBACKS ===
    void handleInput();
    void update();
    void render();
    
    void onAbilityPressed(int abilityIndex);
    void onMouseClick(int x, int y);
    void onRest();
    void onQuit();
};
