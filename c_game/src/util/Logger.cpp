#include "util/AssetManager.hpp"
#include <iostream>
#include <fstream>
#include <ctime>
#include <iomanip>

/**
 * Logger para debug e error reporting
 */

std::string Logger::levelToString(Level level) {
    switch (level) {
        case Level::DEBUG:    return "[DEBUG]";
        case Level::INFO:     return "[INFO]";
        case Level::WARNING:  return "[WARN]";
        case Level::ERROR:    return "[ERROR]";
        case Level::CRITICAL: return "[CRITICAL]";
        default:              return "[UNKNOWN]";
    }
}

void Logger::log(Level level, const std::string& message) {
    std::string logEntry = levelToString(level) + " " + message;
    
    // Log to console
    std::cerr << logEntry << std::endl;
    
    // Log to file
    std::ofstream logFile("kronus_errors.log", std::ios::app);
    if (logFile.is_open()) {
        auto now = std::time(nullptr);
        auto tm = *std::localtime(&now);
        logFile << std::put_time(&tm, "%Y-%m-%d %H:%M:%S") << " " << logEntry << "\n";
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
