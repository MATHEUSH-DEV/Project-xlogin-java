#pragma once

#include <glm/glm.hpp>
#include <memory>
#include <string>

// Forward declarations
class Player;
class Enemy;
class GameWorld;

/**
 * Asset Manager para carregar e cachear recursos
 * (texturas, sons, fonts)
 */
class AssetManager {
public:
    static AssetManager& getInstance();
    
    // TODO: Implementar loading de textures
    // Texture loadTexture(const std::string& path);
    
    // TODO: Implementar loading de sounds  
    // Sound loadSound(const std::string& path);
    
private:
    AssetManager() = default;
};

/**
 * Logger para debug e error reporting
 */
class Logger {
public:
    enum class Level {
        DEBUG,
        INFO,
        WARNING,
        ERROR,
        CRITICAL
    };
    
    static void log(Level level, const std::string& message);
    static void debug(const std::string& msg);
    static void info(const std::string& msg);
    static void warn(const std::string& msg);
    static void error(const std::string& msg);
    static void critical(const std::string& msg);
    
private:
    static std::string levelToString(Level level);
};
