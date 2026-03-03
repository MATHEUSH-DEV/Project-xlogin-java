package game;

import model.Character;

/**
 * Gerencia o primeiro mundo do jogo - Mundo Inicial de Kronus Rift.
 * Aqui os personagens spawn quando são criados.
 */
public class GameWorld {
    private static final String WORLD_NAME = "Floresta de Eldoria";
    private static final String WORLD_DESCRIPTION = "Uma floresta mística e perigosa onde heróis iniciantes começam sua jornada.";

    private Character playerCharacter;
    private int enemiesDefeated = 0;
    private long sessionStartTime;

    public GameWorld(Character character) {
        this.playerCharacter = character;
        this.sessionStartTime = System.currentTimeMillis();
    }

    public String getWorldName() {
        return WORLD_NAME;
    }

    public String getWorldDescription() {
        return WORLD_DESCRIPTION;
    }

    public Character getPlayerCharacter() {
        return playerCharacter;
    }

    /**
     * Simula uma luta contra um inimigo comum.
     * Versão com habilidade especial.
     * 
     * @param enemyName Nome do inimigo
     * @param abilityIndex Índice da habilidade (-1 para ataque normal)
     */
    public void fightEnemy(String enemyName, int abilityIndex) {
        int baseDamage = playerCharacter.getStrength() + (playerCharacter.getAgility() / 2);
        int finalDamage = baseDamage;
        
        // Usar habilidade especial se fornecido índice válido
        if (abilityIndex >= 0) {
            model.Ability ability = playerCharacter.useAbility(abilityIndex);
            if (ability != null) {
                finalDamage = ability.calculateDamage(baseDamage);
            }
        }
        
        int enemyHealth = 50 + (playerCharacter.getLevel() * 10);
        
        // Combate simplificado
        while (enemyHealth > 0 && playerCharacter.getHealth() > 0) {
            // Jogador ataca
            enemyHealth -= finalDamage;
            if (enemyHealth <= 0) break;
            
            // Inimigo ataca
            int enemyDamage = 15 - (playerCharacter.getAgility() / 5);
            playerCharacter.takeDamage(enemyDamage);
        }
        
        if (playerCharacter.getHealth() > 0) {
            enemiesDefeated++;
            long expReward = 100L * playerCharacter.getLevel();
            playerCharacter.addExperience(expReward);
            playerCharacter.heal(30); // Regenera após vitória
        }
    }
    
    /**
     * Simula uma luta contra um inimigo comum (ataque normal, sem habilidade).
     */
    public void fightEnemy(String enemyName) {
        fightEnemy(enemyName, -1);
    }

    public int getEnemiesDefeated() {
        return enemiesDefeated;
    }

    public long getSessionDuration() {
        return (System.currentTimeMillis() - sessionStartTime) / 1000;
    }
}
