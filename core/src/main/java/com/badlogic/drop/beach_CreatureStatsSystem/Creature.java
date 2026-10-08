package com.badlogic.drop.beach_CreatureStatsSystem;

public class Creature {
    private String name;
    private int maxHp;
    private int currentHp;
    private int attack;
    private int defense;
    private int speed;

    public Creature(String name, int maxHp, int attack, int defense, int speed) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    public void takeDamage(int damage) {
        int finalDamage = Math.max(1, damage - defendReduction);

        currentHp -= finalDamage;

        if (currentHp < 0) {
            currentHp = 0;
        }

        // Defend works for one incoming attack only
        clearDefendReduction();
    }


    public boolean isAlive() {
        return currentHp > 0;
    }

    public void reduceSpeed(int amount) {
        speed = Math.max(1, speed - amount);
    }
    public void reduceAttack(int amount) {
        attack = Math.max(0, attack - amount);
    }
    private int defendReduction = 0;
    public void setDefendReduction(int amount) {
        defendReduction = amount;
    }

    public int getDefendReduction() {
        return defendReduction;
    }

    public void clearDefendReduction() {
        defendReduction = 0;
    }
    public void heal (int amount) {
        currentHp = Math.min(maxHp, currentHp + amount);
    }
}
