
package com.badlogic.drop.ot_MoveSkillsSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Creature;

public class MindLockMove extends Move {

    public MindLockMove(String name, int power, String description) {
        super(name, power, description);
    }

    @Override
    public String use(Creature user, Creature target) {
        return user.getName()
            + " uses Mind Lock!\n"
            + "NOTE has been sealed for 3 turns!";
    }
}
