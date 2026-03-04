#include "util/AssetManager.hpp"
#include <iostream>
#include <fstream>
#include <ctime>

AssetManager& AssetManager::getInstance() {
    static AssetManager instance;
    return instance;
}

void Logger::log(Level level, const std::string& message) {
    auto now = std::time(nullptr);
    auto tm = *std::localtime(&now);
    
    char buffer[80];
    std::strftime(buffer, sizeof(buffer), "%Y-%m-%d %H:%M:%S", &tm);
    
    std::string levelStr = levelToString(level);
    std::string output = std::string(buffer) + " [" + levelStr + "] " + message;
    
    // Print to console
    std::cout << output << std::endl;
    
    // Log to file if error/critical
    if (level >= Level::ERROR) {
        std::ofstream logFile("kronus_errors.log", std::ios::app);
        logFile << output << std::endl;
        logFile.close();
    }
}

void Logger::debug(const std::string& msg) {
    log(Level::DEBUG, msg);
}

void Logger::info(const std::string& msg) {
    log(Level::INFO, msg);
}

void Logger::warn(const std::string& msg) {
    log(Level::WARNING, msg);
}

void Logger::error(const std::string& msg) {
    log(Level::ERROR, msg);
}

void Logger::critical(const std::string& msg) {
    log(Level::CRITICAL, msg);
}

std::string Logger::levelToString(Level level) {
    switch (level) {
        case Level::DEBUG: return "DEBUG";
        case Level::INFO: return "INFO";
        case Level::WARNING: return "WARN";
        case Level::ERROR: return "ERROR";
        case Level::CRITICAL: return "CRITICAL";
        default: return "UNKNOWN";
    }
}
