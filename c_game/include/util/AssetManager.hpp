#pragma once

#include <memory>
#include <string>
#include <fstream>

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

