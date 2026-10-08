package com.badlogic.drop.beach_CreatureStatsSystem;

/**
 * An opposing creature. Enemies can talk (dialogue before the fight)
 * and can be flagged as a boss (e.g. the final boss "Ajarn Ken").
 */
public class Enemy extends Creature {
    private final String dialogue;
    private final boolean boss;

    public Enemy(String name, int level, int maxHp, int attack, int defense, int speed,
                 String dialogue, boolean boss) {
        super(name, level, maxHp, attack, defense, speed);
        this.dialogue = dialogue;
        this.boss = boss;
    }

    public Enemy(String name, int level, int maxHp, int attack, int defense, int speed) {
        this(name, level, maxHp, attack, defense, speed, "", false);
    }

    /** Same as above with level 1 and no dialogue. */
    public Enemy(String name, int maxHp, int attack, int defense, int speed) {
        this(name, 1, maxHp, attack, defense, speed);
    }

    public String getDialogue() { return dialogue; }
    public boolean isBoss() { return boss; }
}
