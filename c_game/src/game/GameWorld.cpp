#include "game/GameWorld.hpp"
#include "systems/CombatSystem.hpp"
#include <raylib.h>

GameWorld::GameWorld(std::unique_ptr<Player> p)
    : player(std::move(p)), currentState(GameState::PLAYING) {
}

void GameWorld::update(float deltaTime) {
    if (!player) return;
    
    // === Update Player ===
    player->update(deltaTime);
    
    // === Update Enemies ===
    for (auto& enemy : enemies) {
        if (enemy) {
            enemy->update(deltaTime, player->position);
        }
    }
    
    // === Combat System ===
    CombatSystem::update(deltaTime, *player, enemies);
    
    // === Spawn waves ===
    spawnTimer += deltaTime;
    if (spawnTimer > 10.0f && enemies.size() < 5) {
        spawnEnemyWave(2);
        spawnTimer = 0.0f;
    }
    
    // === Check Game Over ===
    if (player->health <= 0) {
        currentState = GameState::GAME_OVER;
    }
}

void GameWorld::render() {
    // === Render Background ===
    DrawRectangle(0, 0, WORLD_WIDTH, WORLD_HEIGHT, {30, 60, 30, 255});
    
    // === Render Grid (debug) ===
    for (int x = 0; x < WORLD_WIDTH; x += TILE_SIZE) {
        DrawLine(x, 0, x, WORLD_HEIGHT, {50, 100, 50, 100});
    }
    for (int y = 0; y < WORLD_HEIGHT; y += TILE_SIZE) {
        DrawLine(0, y, WORLD_WIDTH, y, {50, 100, 50, 100});
    }
    
    // === Render Entities ===
    if (player) {
        player->render();
    }
    
    for (auto& enemy : enemies) {
        if (enemy) {
            enemy->render();
        }
    }
}

void GameWorld::spawnEnemyWave(int count) {
    CombatSystem::spawnEnemies(count, player->position, enemies);
    waveCount++;
}

void GameWorld::addEnemy(std::unique_ptr<Enemy> enemy) {
    enemies.push_back(std::move(enemy));
}
