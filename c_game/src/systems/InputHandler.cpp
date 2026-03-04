#include "systems/InputHandler.hpp"
#include <raylib.h>

InputHandler::InputHandler() = default;

void InputHandler::update() {
    // === Movimento by-click ===
    if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
        lastMouseX = GetMouseX();
        lastMouseY = GetMouseY();
        if (callbacks.count(Action::MOVE_CLICK)) {
            callbacks[Action::MOVE_CLICK](Action::MOVE_CLICK);
        }
    }
    
    // === Abilities Hotkeys (1,2,3,4) ===
    if (IsKeyPressed(KEY_ONE)) {
        if (callbacks.count(Action::ABILITY_1)) {
            callbacks[Action::ABILITY_1](Action::ABILITY_1);
        }
    }
    if (IsKeyPressed(KEY_TWO)) {
        if (callbacks.count(Action::ABILITY_2)) {
            callbacks[Action::ABILITY_2](Action::ABILITY_2);
        }
    }
    if (IsKeyPressed(KEY_THREE)) {
        if (callbacks.count(Action::ABILITY_3)) {
            callbacks[Action::ABILITY_3](Action::ABILITY_3);
        }
    }
    if (IsKeyPressed(KEY_FOUR)) {
        if (callbacks.count(Action::ABILITY_4)) {
            callbacks[Action::ABILITY_4](Action::ABILITY_4);
        }
    }
    
    // === Outras ações ===
    if (IsKeyPressed(KEY_R)) {
        if (callbacks.count(Action::REST)) {
            callbacks[Action::REST](Action::REST);
        }
    }
    if (IsKeyPressed(KEY_I)) {
        if (callbacks.count(Action::OPEN_INVENTORY)) {
            callbacks[Action::OPEN_INVENTORY](Action::OPEN_INVENTORY);
        }
    }
    if (IsKeyPressed(KEY_U)) {
        if (callbacks.count(Action::TOGGLE_UI)) {
            callbacks[Action::TOGGLE_UI](Action::TOGGLE_UI);
        }
    }
    if (IsKeyPressed(KEY_SPACE)) {
        if (callbacks.count(Action::PAUSE)) {
            callbacks[Action::PAUSE](Action::PAUSE);
        }
    }
    if (IsKeyPressed(KEY_ESCAPE)) {
        if (callbacks.count(Action::QUIT)) {
            callbacks[Action::QUIT](Action::QUIT);
        }
    }
}

void InputHandler::registerCallback(Action action, ActionCallback callback) {
    callbacks[action] = callback;
}

bool InputHandler::isMouseClicked() const {
    return IsMouseButtonPressed(MOUSE_BUTTON_LEFT);
}

int InputHandler::getMouseX() const {
    return lastMouseX;
}

int InputHandler::getMouseY() const {
    return lastMouseY;
}
