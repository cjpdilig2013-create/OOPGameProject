
package com.badlogic.drop.ot_MoveSkillsSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Creature;

public class SpecialSkillMove extends Move {

    public SpecialSkillMove() {
        super(
            "Attack Buff",
            0,
            "Increases attack damage by 50% until the game ends."
        );
    }

    @Override
    public String use(Creature user, Creature target) {
        user.activateAttackBuff();

        return user.getName()
            + " used Attack Buff!"
            + "\nBuff Stacks: " + user.getAttackBuffStacks()
            + "\nDamage Multiplier: "
            + (100 + user.getAttackBuffStacks() * 50) + "%";
    }
}
