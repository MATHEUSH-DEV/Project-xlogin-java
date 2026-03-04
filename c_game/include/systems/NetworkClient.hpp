#pragma once

#include <string>
#include <memory>

/**
 * Cliente de rede para sincronizar com backend Java
 * Comunica via REST API (http://localhost:8080)
 */
class NetworkClient {
public:
    NetworkClient(const std::string& serverUrl = "http://localhost:8080");
    
    // === AUTENTICAÇÃO ===
    bool authenticate(const std::string& username, const std::string& password);
    bool logout();
    
    // === CHARACTER ===
    std::string loadCharacter(const std::string& characterName);
    bool saveCharacter(const std::string& jsonData);
    
    // === COMBATE ===
    bool reportCombatResult(const std::string& enemyName, int damageDealt, bool victory);
    
    // === SYNC ===
    bool syncPlayerStats(int level, int hp, int mana, int xp);
    
    bool isConnected() const { return connected; }
    
private:
    std::string serverUrl;
    bool connected = false;
    std::string sessionToken;
    
    std::string performRequest(const std::string& method, 
                               const std::string& endpoint,
                               const std::string& body = "");
};
