package com.badlogic.drop.beach_CreatureStatsSystem;

/**
 * Base class for everything that fights (PlayerCreature and Enemy).
 * Other systems (Move, Battle, BattleScreen) should only talk to creatures
 * through these methods instead of touching stats directly.
 */
public class Creature {
    private final String name;
    private int level;
    protected final Stats stats;

    // Set by DefendMove: reduces the next incoming hit, then resets.
    private int defendReduction = 0;

    public Creature(String name, int level, int maxHp, int attack, int defense, int speed) {
        this.name = name;
        this.level = Math.max(1, level);
        this.stats = new Stats(maxHp, attack, defense, speed);
    }

    /** Same as above with level 1. */
    public Creature(String name, int maxHp, int attack, int defense, int speed) {
        this(name, 1, maxHp, attack, defense, speed);
    }

    /**
     * Subtracts HP (never below 0). The amount is the damage already worked out by
     * the Move; a pending defend reduction is applied here, and it only lasts for
     * one incoming hit. A hit that lands always deals at least 1.
     * @return the damage actually dealt
     */
    public int takeDamage(int amount) {
        if (amount <= 0) return 0;
        int finalDamage = Math.max(1, amount - defendReduction);
        int before = stats.getHp();
        stats.setHp(before - finalDamage);
        clearDefendReduction();
        return before - stats.getHp();
    }

    /**
     * Restores HP (never above max HP).
     * @return the HP actually restored
     */
    public int heal(int amount) {
        if (amount <= 0) return 0;
        int before = stats.getHp();
        stats.setHp(before + amount);
        return stats.getHp() - before;
    }

    public boolean isAlive() { return stats.getHp() > 0; }

    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getHp() { return stats.getHp(); }
    public int getMaxHp() { return stats.getMaxHp(); }
    public int getAttack() { return stats.getAttack(); }
    public int getDefense() { return stats.getDefense(); }
    public int getSpeed() { return stats.getSpeed(); }

    /** Same as getHp(); kept because Move/Battle code uses this name. */
    public int getCurrentHp() { return getHp(); }

    /** Lets moves apply buffs/debuffs (e.g. Puddle Slip lowers speed). */
    public void setAttack(int attack) { stats.setAttack(attack); }
    public void setDefense(int defense) { stats.setDefense(defense); }
    public void setSpeed(int speed) { stats.setSpeed(speed); }

    /** Speed never drops below 1. */
    public void reduceSpeed(int amount) { stats.setSpeed(Math.max(1, stats.getSpeed() - amount)); }

    /** Attack never drops below 0. */
    public void reduceAttack(int amount) { stats.setAttack(Math.max(0, stats.getAttack() - amount)); }

    public void setDefendReduction(int amount) { defendReduction = Math.max(0, amount); }
    public int getDefendReduction() { return defendReduction; }
    public void clearDefendReduction() { defendReduction = 0; }

    protected void setLevel(int level) { this.level = Math.max(1, level); }

    @Override
    public String toString() {
        return name + " Lv." + level + " [" + stats + "]";
    }
}
