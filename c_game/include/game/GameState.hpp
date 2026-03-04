#pragma once

#include <string>

/**
 * Estados do jogo
 */
enum class GameState {
    LOADING,      // Carregando dados
    PLAYING,      // Jogando normalmente
    COMBAT,       // Em combate ativo
    PAUSE,        // Jogo pausado
    GAME_OVER,    // Morreu
    VICTORY,      // Venceu o nível/boss
    MENU,         // Menu principal
    INVENTORY,    // Tela de inventário
    QUIT          // Saindo do jogo
};

/**
 * Converter enum para string
 */
inline std::string GameStateToString(GameState state) {
    switch (state) {
        case GameState::LOADING: return "LOADING";
        case GameState::PLAYING: return "PLAYING";
        case GameState::COMBAT: return "COMBAT";
        case GameState::PAUSE: return "PAUSE";
        case GameState::GAME_OVER: return "GAME_OVER";
        case GameState::VICTORY: return "VICTORY";
        case GameState::MENU: return "MENU";
        case GameState::INVENTORY: return "INVENTORY";
        case GameState::QUIT: return "QUIT";
        default: return "UNKNOWN";
    }
}
