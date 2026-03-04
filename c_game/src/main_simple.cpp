#include <raylib.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>

// Versão simplificada do game enquanto a compilação é corrigida
int main() {
    // Initialize window
    InitWindow(1600, 900, "Kronus Rift - Floresta de Eldoria");
    SetTargetFPS(60);

    // Load character JSON if it exists
    std::string characterName = "Herói";
    std::ifstream charFile(std::string(std::getenv("TEMP")) + "/kronus_char_*");

    while (!WindowShouldClose()) {
        // Update

        // Render
        BeginDrawing();
        ClearBackground(Color{20, 20, 30, 255});
        
        // Draw title
        DrawText("Kronus Rift - Game Engine", 50, 50, 40, Color{255, 200, 100, 255});
        DrawText("Compilacao em progresso...", 50, 100, 20, Color{150, 150, 150, 255});
        DrawText("Pressione ESC para sair", 50, 150, 16, Color{100, 200, 255, 255});

        EndDrawing();
    }

    CloseWindow();
    return 0;
}
