#include "util/AssetManager.hpp"

AssetManager& AssetManager::getInstance() {
    static AssetManager instance;
    return instance;
}

