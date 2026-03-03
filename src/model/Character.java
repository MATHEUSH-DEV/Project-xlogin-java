package model;

import java.io.Serializable;

/**
 * Representa um personagem do jogador.
 */
public class Character implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String race;
    private String clazz;
    private long createdAt;

    public Character(String name, String race, String clazz) {
        this.name = name;
        this.race = race;
        this.clazz = clazz;
        this.createdAt = System.currentTimeMillis();
    }

    public String getName() { return name; }
    public String getRace() { return race; }
    public String getClazz() { return clazz; }
    public long getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.format("Character{name='%s', race='%s', class='%s'}", name, race, clazz);
    }
}
