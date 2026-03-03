package model;

import java.io.Serializable;

/**
 * Representa uma habilidade especial de combate.
 * Cada personagem tem múltiplas habilidades baseadas em sua classe.
 * 
 * Exemplo:
 * - Guerreiro: Golpe Poderoso (150% dano, 20 mana, 3s cooldown)
 * - Bruxo: Bola de Fogo (170% dano, 25 mana, 2.5s cooldown)
 */
public class Ability implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private int manaCost;
    private double damageMultiplier;  // 1.5 = 150% do dano base
    private long cooldownMs;          // Cooldown em milissegundos
    private long lastUsedTime = 0;
    
    /**
     * Cria uma nova habilidade.
     * 
     * @param name Nome da habilidade (ex: "Golpe Poderoso")
     * @param description Descrição (ex: "Ataque devastador")
     * @param manaCost Custo em mana (ex: 20)
     * @param damageMultiplier Multiplicador de dano (ex: 1.5 para 150%)
     * @param cooldownMs Cooldown em milissegundos (ex: 3000 para 3 segundos)
     */
    public Ability(String name, String description, int manaCost, double damageMultiplier, long cooldownMs) {
        this.name = name;
        this.description = description;
        this.manaCost = manaCost;
        this.damageMultiplier = damageMultiplier;
        this.cooldownMs = cooldownMs;
    }
    
    /**
     * Verifica se a habilidade pode ser usada.
     * 
     * @param currentMana Mana atual do personagem
     * @return true se houver mana suficiente e cooldown passou
     */
    public boolean canUse(int currentMana) {
        long now = System.currentTimeMillis();
        boolean hasEnoughMana = currentMana >= manaCost;
        boolean cooldownExpired = (now - lastUsedTime) >= cooldownMs;
        return hasEnoughMana && cooldownExpired;
    }
    
    /**
     * Marca a habilidade como usada (inicia cooldown).
     */
    public void use() {
        this.lastUsedTime = System.currentTimeMillis();
    }
    
    /**
     * Calcula o dano final da habilidade.
     * 
     * @param baseDamage Dano base do personagem (STR + AGI/2)
     * @return Dano final (baseDamage × damageMultiplier)
     */
    public int calculateDamage(int baseDamage) {
        return (int) (baseDamage * damageMultiplier);
    }
    
    /**
     * Retorna o tempo restante de cooldown em milissegundos.
     * 
     * @return ms de cooldown restante, ou 0 se disponível
     */
    public long getRemainingCooldown() {
        long elapsed = System.currentTimeMillis() - lastUsedTime;
        long remaining = cooldownMs - elapsed;
        return Math.max(0, remaining);
    }
    
    /**
     * Verifica se a habilidade está em cooldown.
     * 
     * @return true se em cooldown
     */
    public boolean isOnCooldown() {
        return (System.currentTimeMillis() - lastUsedTime) < cooldownMs;
    }
    
    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getManaCost() { return manaCost; }
    public double getDamageMultiplier() { return damageMultiplier; }
    public long getCooldownMs() { return cooldownMs; }
    public long getLastUsedTime() { return lastUsedTime; }
    
    @Override
    public String toString() {
        return String.format("Ability{name='%s', mana=%d, dmg=%.1fx, cd=%.1fs}", 
            name, manaCost, damageMultiplier, cooldownMs / 1000.0);
    }
}
