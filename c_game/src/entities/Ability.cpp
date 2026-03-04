#include "entities/Ability.hpp"
#include <chrono>
#include <cmath>

bool Ability::canUse(int currentMana) const {
    auto now = std::chrono::high_resolution_clock::now();
    auto elapsed = std::chrono::duration_cast<std::chrono::milliseconds>(
        now.time_since_epoch()).count() - lastUsedTime;
    
    bool hasMana = currentMana >= manaCost;
    bool offCooldown = elapsed >= cooldownMs;
    return hasMana && offCooldown;
}

void Ability::use() {
    auto now = std::chrono::high_resolution_clock::now();
    lastUsedTime = std::chrono::duration_cast<std::chrono::milliseconds>(
        now.time_since_epoch()).count();
}

long Ability::getRemainingCooldown() const {
    auto now = std::chrono::high_resolution_clock::now();
    auto elapsed = std::chrono::duration_cast<std::chrono::milliseconds>(
        now.time_since_epoch()).count() - lastUsedTime;
    
    long remaining = cooldownMs - elapsed;
    return std::max(0L, remaining);
}

int Ability::calculateDamage(int baseDamage) const {
    return static_cast<int>(baseDamage * damageMultiplier);
}
