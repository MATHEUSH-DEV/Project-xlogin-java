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
     */
    public void fightEnemy(String enemyName) {
        int baseDamage = 15;
        int playerDamage = playerCharacter.getStrength() + (playerCharacter.getAgility() / 2);
        int enemyHealth = 50 + (playerCharacter.getLevel() * 10);
        
        // Combate simplificado
        while (enemyHealth > 0 && playerCharacter.getHealth() > 0) {
            // Jogador ataca
            enemyHealth -= playerDamage;
            if (enemyHealth <= 0) break;
            
            // Inimigo ataca
            int enemyDamage = baseDamage - (playerCharacter.getAgility() / 5);
            playerCharacter.takeDamage(enemyDamage);
        }
        
        if (playerCharacter.getHealth() > 0) {
            enemiesDefeated++;
            long expReward = 100L * playerCharacter.getLevel();
            playerCharacter.addExperience(expReward);
            playerCharacter.heal(30); // Regenera após vitória
        }
    }

    public int getEnemiesDefeated() {
        return enemiesDefeated;
    }

    public long getSessionDuration() {
        return (System.currentTimeMillis() - sessionStartTime) / 1000;
    }
}
