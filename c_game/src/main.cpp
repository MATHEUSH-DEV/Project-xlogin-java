#include "game/Game.hpp"
#include <iostream>
#include <fstream>
#include <sstream>
#include <filesystem>
#include <cstdlib>

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
    // Tentar carregar do arquivo temp criado pelo Java
    std::string tempPath = std::string(std::getenv("TEMP") ? std::getenv("TEMP") : "/tmp");
    
    // Procurar arquivos kronus_char_*.json
    for (const auto& entry : std::filesystem::directory_iterator(tempPath)) {
        std::string filename = entry.path().filename().string();
        if (filename.find("kronus_char_") == 0 && filename.find(".json") != std::string::npos) {
            std::ifstream file(entry.path());
            std::stringstream buffer;
            buffer << file.rdbuf();
            std::cout << "[Game] Carregado character JSON: " << filename << std::endl;
            return buffer.str();
        }
    }
    
    // Fallback: dummy JSON se arquivo não encontrado
    std::cout << "[Game] Arquivo de character não encontrado, usando dummy data" << std::endl;
    std::string jsonData = R"({
        "name": "Herói",
        "race": "Humano",
        "class": "Guerreiro",
        "level": 1,
        "experience": 0,
        "stats": {
            "strength": 21,
            "agility": 10,
            "intelligence": 8
        },
        "health": 142,
        "mana": 66,
        "abilities": [
            {
                "name": "Golpe Poderoso",
                "description": "Ataque devastador que causa 150% de dano",
                "manaCost": 20,
                "damageMultiplier": 1.5,
                "cooldownMs": 3000
            },
            {
                "name": "Escudo do Guerreiro",
                "description": "Aumenta defesa em 50%",
                "manaCost": 15,
                "damageMultiplier": 0.8,
                "cooldownMs": 4000
            },
            {
                "name": "Golpe Duplo",
                "description": "Dois ataques rápidos",
                "manaCost": 25,
                "damageMultiplier": 1.2,
                "cooldownMs": 2500
            },
            {
                "name": "Berserk",
                "description": "Aumenta dano em 100%",
                "manaCost": 40,
                "damageMultiplier": 2.0,
                "cooldownMs": 8000
            }
        ]
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
