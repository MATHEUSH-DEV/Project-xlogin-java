#pragma once

#include <string>
#include <map>
#include <functional>

/**
 * Gerencia input do teclado/mouse
 * Implementa hotkeys para abilities (1,2,3,4) e movimento by-click
 */
class InputHandler {
public:
    enum class Action {
        MOVE_CLICK,      // Mouse click para movimento
        ABILITY_1,       // Tecla 1
        ABILITY_2,       // Tecla 2
        ABILITY_3,       // Tecla 3
        ABILITY_4,       // Tecla 4
        REST,            // Tecla R
        OPEN_INVENTORY,  // Tecla I
        TOGGLE_UI,       // Tecla U
        PAUSE,           // Espaço
        QUIT             // ESC
    };

    using ActionCallback = std::function<void(Action)>;

public:
    InputHandler();
    
    void update();
    void registerCallback(Action action, ActionCallback callback);
    
    bool isMouseClicked() const;
    int getMouseX() const;
    int getMouseY() const;
    
private:
    std::map<Action, ActionCallback> callbacks;
    int lastMouseX = 0;
    int lastMouseY = 0;
};
