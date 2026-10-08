package com.badlogic.drop.ot_MoveSkillsSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Creature;

public abstract class Move {
    private final String name;
    private final int power;
    private final String description;

    public Move(String name, int power, String description) {
        this.name = name;
        this.power = power;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getPower() {
        return power;
    }

    public String getDescription() {
        return description;
    }

    public abstract String use(Creature user, Creature target);
}
