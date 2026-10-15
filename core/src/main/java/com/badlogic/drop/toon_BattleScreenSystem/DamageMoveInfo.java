package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.drop.ot_MoveSkillsSystem.DamageMove;
import com.badlogic.drop.ot_MoveSkillsSystem.Move;

//Info for a normal attack
public class DamageMoveInfo extends MoveInfo {
    private final int power;
    private final String description;

    public DamageMoveInfo(String name, int power, String description) {
        super(name);
        this.power = power;
        this.description = description;
    }

    @Override
    public Move createMove() {
        return new DamageMove(getName(), power, description);
    }
}
