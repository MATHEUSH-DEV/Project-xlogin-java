#pragma once

#include <string>
#include <vector>
#include <glm/glm.hpp>

/**
 * Representa uma habilidade especial do jogador
 * Mapeia diretamente do sistema Java: Ability.java
 */
struct Ability {
    std::string name;
    std::string description;
    int manaCost;
    float damageMultiplier;
    long cooldownMs;
    long lastUsedTime;
    int hotkey;  // 1, 2, 3, 4

    Ability(const std::string& n, const std::string& desc, int mana,
            float dmgMult, long cd, int key)
        : name(n), description(desc), manaCost(mana),
          damageMultiplier(dmgMult), cooldownMs(cd),
          lastUsedTime(0), hotkey(key) {}

    bool canUse(int currentMana) const;
    void use();
    long getRemainingCooldown() const;
    int calculateDamage(int baseDamage) const;
};
