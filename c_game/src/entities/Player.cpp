#include "entities/Player.hpp"
#include <nlohmann/json.hpp>
#include <cmath>

using json = nlohmann::json;

Player::Player(const std::string& n, const std::string& r, const std::string& c)
    : name(n), race(r), clazz(c) {
    initializeAbilities();
    calculateStats();
}

void Player::initializeAbilities() {
    abilities.clear();
    
    if (clazz == "Guerreiro") {
        abilities.push_back(Ability("Golpe Poderoso", 
            "Ataque devastador que causa 150% de dano", 20, 1.5f, 3000, 1));
        abilities.push_back(Ability("Fúria Berserker",
            "Ataque selvagem que causa 200% de dano", 50, 2.0f, 10000, 2));
        abilities.push_back(Ability("Defesa Heroica",
            "Reduz dano recebido em 50%", 30, 0.5f, 5000, 3));
        abilities.push_back(Ability("Investida",
            "Carrega inimigos causando 120% dano e afastamento", 25, 1.2f, 4000, 4));
    }
    else if (clazz == "Caçador") {
        abilities.push_back(Ability("Tiro Preciso",
            "Ataque preciso que causa 120% de dano", 15, 1.2f, 2000, 1));
        abilities.push_back(Ability("Chuva de Flechas",
            "Múltiplos tiros causam 140% de dano", 30, 1.4f, 5000, 2));
        abilities.push_back(Ability("Camuflar",
            "Fica invisível por 5s (sem dano)", 20, 0.0f, 8000, 3));
        abilities.push_back(Ability("Disparos Automáticos",
            "Rajada rápida causando 100% dano", 25, 1.0f, 3000, 4));
    }
    else if (clazz == "Bruxo") {
        abilities.push_back(Ability("Bola de Fogo",
            "Fogo mágico que causa 170% de dano", 25, 1.7f, 2500, 1));
        abilities.push_back(Ability("Maldição Sombria",
            "Magia sombria que causa 190% de dano", 40, 1.9f, 8000, 2));
        abilities.push_back(Ability("Escudo de Mana",
            "Absorve 100 dano usando mana", 35, 0.0f, 6000, 3));
        abilities.push_back(Ability("Explosão Arcana",
            "Explosão mágica causando 180% dano em área", 45, 1.8f, 7000, 4));
    }
}

void Player::calculateStats() {
    maxHealth = 100 + (strength * 2);
    maxMana = 50 + (intelligence * 2);
    if (health == 0) health = maxHealth;  // Initial only
    if (mana == 0) mana = maxMana;
}

void Player::update(float deltaTime) {
    // === Movimento ===
    if (glm::distance(position, targetPosition) > 5.0f) {
        glm::vec2 direction = glm::normalize(targetPosition - position);
        position += direction * moveSpeed * deltaTime;
    } else {
        position = targetPosition;
    }
    
    // === Animação ===
    frameCounter++;
    if (frameCounter % 10 == 0) {
        currentFrame = (currentFrame + 1) % 4;  // 4 frames de animação
    }
}

void Player::render() {
    // Renderização do sprite do personagem
    // TODO: Carregar sprite sheet baseado na raça/classe
    DrawRectangle(
        static_cast<int>(position.x - radius),
        static_cast<int>(position.y - radius),
        static_cast<int>(radius * 2),
        static_cast<int>(radius * 2),
        {255, 200, 100, 255}  // Cor temporária
    );
    
    // Health bar acima do personagem
    DrawRectangle(
        static_cast<int>(position.x - 25),
        static_cast<int>(position.y - 40),
        50, 5,
        RED
    );
    int healthWidth = static_cast<int>(50.0f * health / maxHealth);
    DrawRectangle(
        static_cast<int>(position.x - 25),
        static_cast<int>(position.y - 40),
        healthWidth, 5,
        GREEN
    );
}

void Player::moveTo(glm::vec2 targetPos) {
    targetPosition = targetPos;
}

void Player::addExperience(int exp) {
    experience += exp;
    // TODO: implementar verificação de level-up (1000 XP por level)
    if (experience >= 1000 * level) {
        levelUp();
    }
}

void Player::levelUp() {
    level++;
    strength += 2;
    agility += 1;
    intelligence += 2;
    calculateStats();
    health = maxHealth;  // Full heal on level up
    mana = maxMana;
}

void Player::takeDamage(int damage) {
    // Redução de dano baseada em AGI
    int mitigated = damage - (agility / 5);
    health -= std::max(1, mitigated);
}

void Player::heal(int amount) {
    health = std::min(health + amount, maxHealth);
}

void Player::restoreMana(int amount) {
    mana = std::min(mana + amount, maxMana);
}

bool Player::canUseAbility(int abilityIndex) const {
    if (abilityIndex < 0 || abilityIndex >= static_cast<int>(abilities.size())) {
        return false;
    }
    return abilities[abilityIndex].canUse(mana);
}

Ability* Player::useAbility(int abilityIndex) {
    if (!canUseAbility(abilityIndex)) {
        return nullptr;
    }
    
    Ability& ability = abilities[abilityIndex];
    mana -= ability.manaCost;
    ability.use();
    return &ability;
}

void Player::loadFromJSON(const std::string& jsonData) {
    try {
        auto data = json::parse(jsonData);
        
        name = data["name"];
        race = data["race"];
        clazz = data["clazz"];
        level = data["level"];
        experience = data["experience"];
        strength = data["strength"];
        agility = data["agility"];
        intelligence = data["intelligence"];
        health = data["health"];
        mana = data["mana"];
        enemiesDefeated = data.value("enemiesDefeated", 0);
        
        calculateStats();
        initializeAbilities();
    } catch (const std::exception& e) {
        // Log error
    }
}

std::string Player::toJSON() const {
    json data;
    data["name"] = name;
    data["race"] = race;
    data["clazz"] = clazz;
    data["level"] = level;
    data["experience"] = experience;
    data["strength"] = strength;
    data["agility"] = agility;
    data["intelligence"] = intelligence;
    data["health"] = health;
    data["mana"] = mana;
    data["maxHealth"] = maxHealth;
    data["maxMana"] = maxMana;
    data["enemiesDefeated"] = enemiesDefeated;
    data["position"] = {{"x", position.x}, {"y", position.y}};
    
    return data.dump();
}
