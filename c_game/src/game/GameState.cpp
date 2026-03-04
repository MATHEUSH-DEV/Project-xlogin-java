#include "game/GameState.hpp"
#include <string>

std::string gameStateToString(GameState state) {
    switch (state) {
        case GameState::LOADING:      return "LOADING";
        case GameState::MENU:         return "MENU";
        case GameState::PLAYING:      return "PLAYING";
        case GameState::COMBAT:       return "COMBAT";
        case GameState::PAUSE:        return "PAUSE";
        case GameState::INVENTORY:    return "INVENTORY";
        case GameState::GAME_OVER:    return "GAME_OVER";
        case GameState::VICTORY:      return "VICTORY";
        case GameState::QUIT:         return "QUIT";
        default:                      return "UNKNOWN";
    }
}
