#pragma once

#include <string>
#include <fstream>

/**
 * Logger - Sistema de logging com arquivo
 */
class Logger {
public:
    enum class Level {
        DEBUG,
        INFO,
        WARN,
        ERROR,
        CRITICAL
    };
    
    static void log(Level level, const std::string& message);
    static void debug(const std::string& message);
    static void info(const std::string& message);
    static void warn(const std::string& message);
    static void error(const std::string& message);
    static void critical(const std::string& message);
    
private:
    static std::string levelToString(Level level);
    static const std::string LOG_FILE;
};

