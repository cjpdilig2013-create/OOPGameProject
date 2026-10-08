package com.badlogic.drop.ot_MoveSkillsSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Creature;

public class DefendMove extends Move {

    public DefendMove(String name, int power, String description) {
        super(name, power, description);
    }

    @Override
    public String use(Creature user, Creature target) {

        user.setDefendReduction(getPower());

        return user.getName() + " used " + getName()
            + "!"
            + "\nDefense increased by " + getPower()
            + " for 1 turn.";
    }
}
