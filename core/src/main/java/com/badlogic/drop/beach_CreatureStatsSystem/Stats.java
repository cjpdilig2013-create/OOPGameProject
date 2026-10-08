package com.badlogic.drop.beach_CreatureStatsSystem;

/**
 * Holds the numeric stats of a creature.
 * Creature owns one Stats object and delegates HP/Attack/Defense/Speed to it.
 */
public class Stats {
    private int maxHp;
    private int hp;
    private int attack;
    private int defense;
    private int speed;

    public Stats(int maxHp, int attack, int defense, int speed) {
        this.maxHp = Math.max(1, maxHp);
        this.hp = this.maxHp;
        this.attack = Math.max(0, attack);
        this.defense = Math.max(0, defense);
        this.speed = Math.max(0, speed);
    }

    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getSpeed() { return speed; }

    /** Keeps HP inside 0..maxHp. */
    public void setHp(int hp) { this.hp = Math.max(0, Math.min(hp, maxHp)); }

    /** Changing max HP also clamps current HP. */
    public void setMaxHp(int maxHp) {
        this.maxHp = Math.max(1, maxHp);
        this.hp = Math.min(this.hp, this.maxHp);
    }

    // Setters so moves (e.g. Puddle Slip lowering speed) can modify stats.
    public void setAttack(int attack) { this.attack = Math.max(0, attack); }
    public void setDefense(int defense) { this.defense = Math.max(0, defense); }
    public void setSpeed(int speed) { this.speed = Math.max(0, speed); }

    @Override
    public String toString() {
        return "HP " + hp + "/" + maxHp + " ATK " + attack + " DEF " + defense + " SPD " + speed;
    }
}
