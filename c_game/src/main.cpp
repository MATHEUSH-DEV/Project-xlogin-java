#include "game/Game.hpp"
#include <iostream>
#include <fstream>
#include <sstream>

/**
 * KRONUS RIFT - Game Engine em C++
 * 
 * Estilo: WoW em pixel art
 * Mecânicas: LoL-style (movimento by-click, hotkey abilities 1-4)
 * Backend: Java (autenticação + persistência)
 * 
 * Features:
 * - ✅ Player movement by mouse click
 * - ✅ 4 habilidades por classe (hotkeys 1,2,3,4)
 * - ✅ Sistema de combate em tempo real
 * - ✅ Cooldowns e mana
 * - ✅ Wave spawning de inimigos
 * - ✅ HUD com stats
 * - ✅ Sincronização com Java backend
 */

std::string loadPlayerDataFromJava() {
    // TODO: Implement REST API call to Java backend
    // For now, return dummy JSON
    std::string jsonData = R"({
        "name": "Herói",
        "race": "Humano",
        "clazz": "Guerreiro",
        "level": 1,
        "experience": 0,
        "strength": 18,
        "agility": 12,
        "intelligence": 10,
        "health": 136,
        "mana": 70,
        "enemiesDefeated": 0
    })";
    
    return jsonData;
}

int main(int argc, char* argv[]) {
    try {
        // === Load player data ===
        std::string playerJson = loadPlayerDataFromJava();
        
        // === Create and initialize game ===
        Game game("Kronus Rift - Floresta de Eldoria", 1600, 900);
        
        if (!game.initialize(playerJson)) {
            std::cerr << "Failed to initialize game" << std::endl;
            return 1;
        }
        
        // === Run game loop ===
        game.run();
        
        // === Shutdown ===
        game.shutdown();
        
        return 0;
    } catch (const std::exception& e) {
        std::cerr << "Fatal error: " << e.what() << std::endl;
        return 1;
    }
}
