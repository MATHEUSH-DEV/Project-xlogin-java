#include "game/Game.hpp"
#include "systems/CombatSystem.hpp"
#include <raylib.h>
#include <iostream>

Game::Game(const std::string& gameTitle, int width, int height)
    : title(gameTitle), screenWidth(width), screenHeight(height),
      inputHandler(std::make_unique<InputHandler>()),
      networkClient(std::make_unique<NetworkClient>()),
      uiManager(std::make_unique<UIManager>()) {
}

Game::~Game() {
    shutdown();
}

bool Game::initialize(const std::string& playerJson) {
    // === Initialize Raylib ===
    InitWindow(screenWidth, screenHeight, title.c_str());
    SetTargetFPS(static_cast<int>(targetFPS));
    
    // === Create Player ===
    auto player = std::make_unique<Player>("Herói", "Humano", "Guerreiro");
    player->loadFromJSON(playerJson);
    
    // === Create World ===
    world = std::make_unique<GameWorld>(std::move(player));
    
    // === Register Input Callbacks ===
    inputHandler->registerCallback(InputHandler::Action::MOVE_CLICK,
        [this](InputHandler::Action) {
            int mx = inputHandler->getMouseX();
            int my = inputHandler->getMouseY();
            onMouseClick(mx, my);
        }
    );
    
    inputHandler->registerCallback(InputHandler::Action::ABILITY_1,
        [this](InputHandler::Action) { onAbilityPressed(0); });
    inputHandler->registerCallback(InputHandler::Action::ABILITY_2,
        [this](InputHandler::Action) { onAbilityPressed(1); });
    inputHandler->registerCallback(InputHandler::Action::ABILITY_3,
        [this](InputHandler::Action) { onAbilityPressed(2); });
    inputHandler->registerCallback(InputHandler::Action::ABILITY_4,
        [this](InputHandler::Action) { onAbilityPressed(3); });
    
    inputHandler->registerCallback(InputHandler::Action::REST,
        [this](InputHandler::Action) { onRest(); });
    
    inputHandler->registerCallback(InputHandler::Action::QUIT,
        [this](InputHandler::Action) { onQuit(); });
    
    running = true;
    return true;
}

void Game::run() {
    while (running && !WindowShouldClose()) {
        deltaTime = GetFrameTime();
        totalElapsedTime += deltaTime;
        
        handleInput();
        update();
        render();
    }
}

void Game::shutdown() {
    if (world && world->getPlayer()) {
        // Sincronizar stats finais com backend
        Player* player = world->getPlayer();
        networkClient->syncPlayerStats(
            player->level,
            player->health,
            player->mana,
            player->experience
        );
    }
    
    CloseWindow();
}

void Game::handleInput() {
    inputHandler->update();
}

void Game::update() {
    world->update(deltaTime);
    uiManager->update(*world->getPlayer());
}

void Game::render() {
    BeginDrawing();
    ClearBackground({30, 30, 50, 255});
    
    // === Render World ===
    world->render();
    
    // === Render UI ===
    uiManager->render(*world->getPlayer());
    
    // === FPS Counter (debug) ===
    DrawFPS(10, 10);
    
    EndDrawing();
}

void Game::onAbilityPressed(int abilityIndex) {
    Player* player = world->getPlayer();
    if (!player || world->getEnemies().empty()) return;
    
    Ability* ability = player->useAbility(abilityIndex);
    if (ability) {
        // Usar habilidade no primeiro inimigo disponível
        auto& enemies = world->getEnemies();
        if (!enemies.empty() && enemies[0]) {
            CombatSystem::playerUseAbility(*player, *enemies[0], *ability);
        }
    }
}

void Game::onMouseClick(int x, int y) {
    Player* player = world->getPlayer();
    if (!player) return;
    
    glm::vec2 clickPos(static_cast<float>(x), static_cast<float>(y));
    player->moveTo(clickPos);
}

void Game::onRest() {
    Player* player = world->getPlayer();
    if (player) {
        player->heal(50);
        player->restoreMana(30);
    }
}

void Game::onQuit() {
    running = false;
}
