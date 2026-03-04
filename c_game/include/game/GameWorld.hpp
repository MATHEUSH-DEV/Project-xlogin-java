#pragma once

#include "entities/Player.hpp"
#include "entities/Enemy.hpp"
#include <vector>
#include <memory>

enum class GameState {
    LOADING,
    PLAYING,
    COMBAT,
    PAUSE,
    GAME_OVER,
    VICTORY
};

/**
 * Mundo do jogo - Floresta de Eldoria
 * Gerencia mundo, inimigos, spawn, eventos
 */
class GameWorld {
public:
    static constexpr int WORLD_WIDTH = 2560;    // pixels
    static constexpr int WORLD_HEIGHT = 1440;   // pixels
    
    // Tile-based
    static constexpr int TILE_SIZE = 32;
    static constexpr int TILES_X = WORLD_WIDTH / TILE_SIZE;
    static constexpr int TILES_Y = WORLD_HEIGHT / TILE_SIZE;

public:
    GameWorld(std::unique_ptr<Player> player);
    
    void update(float deltaTime);
    void render();
    
    void spawnEnemyWave(int count);
    void addEnemy(std::unique_ptr<Enemy> enemy);
    
    GameState getState() const { return currentState; }
    void setState(GameState state) { currentState = state; }
    
    Player* getPlayer() { return player.get(); }
    const std::vector<std::unique_ptr<Enemy>>& getEnemies() const { return enemies; }
    
private:
    std::unique_ptr<Player> player;
    std::vector<std::unique_ptr<Enemy>> enemies;
    GameState currentState = GameState::LOADING;
    
    float spawnTimer = 0.0f;
    int waveCount = 0;
    int enemiesDefeatedThisWave = 0;
};
