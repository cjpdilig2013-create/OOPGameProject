
package com.badlogic.drop.ot_MoveSkillsSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Creature;

public class DefendMove extends Move {

    public DefendMove(String name, int power, String description) {
        super(name, power, description);
    }

    @Override
    public String use(Creature user, Creature target) {
        user.setDefendReduction(50);

        return user.getName() + " used " + getName()
            + "!"
            + "\nShield activated!"
            + "\nDamage reduction: 50%"
            + "\nShield remains until hit.";
    }
}
