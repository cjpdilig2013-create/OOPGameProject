package com.badlogic.drop.ot_MoveSkillsSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Creature;
import com.badlogic.drop.ot_MoveSkillsSystem.EnemyBuffMove;

public class EnemyBuffMove extends Move {

    public EnemyBuffMove(String name, int power, String description) {
        super(name, power, description);
    }

    @Override
    public String use(Creature user, Creature target) {
        user.setAttack(user.getAttack() + getPower());

        return user.getName() + " used Buff!\n"
            + "Attack increased by " + getPower() + "!";
    }

}
