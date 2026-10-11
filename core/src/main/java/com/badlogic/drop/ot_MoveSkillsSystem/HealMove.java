
package com.badlogic.drop.ot_MoveSkillsSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Creature;

public class HealMove extends Move {

    public HealMove(String name, int power, String description) {
        super(name, power, description);
    }

    @Override
    public String use(Creature user, Creature target) {
        int healed = user.heal(getPower());

        if (healed == 0) {
            return user.getName() + " used " + getName()
                + ", but recovered no HP!";
        }

        return user.getName() + " used " + getName()
            + " and recovered " + healed + " HP!";
    }
}
