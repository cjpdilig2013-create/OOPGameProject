package com.badlogic.drop.ot_MoveSkillsSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Creature;

public class HealMove extends Move {

    public HealMove(String name, int power, String description) {
        super(name, power, description);
    }

    @Override
    public String use(Creature user, Creature target) {
        int oldHp = user.getCurrentHp();

        user.heal(getPower());

        int healedAmount = user.getCurrentHp() - oldHp;

        return user.getName() + " used " + getName()
            + "!"
            + "\nHealed " + healedAmount + " HP."
            + "\n" + user.getName() + " HP: "
            + user.getCurrentHp() + "/" + user.getMaxHp();
    }
}
