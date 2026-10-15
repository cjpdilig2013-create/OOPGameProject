package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.drop.ot_MoveSkillsSystem.ChargeMove;
import com.badlogic.drop.ot_MoveSkillsSystem.Move;

//Info for an attack that needs a turn to charge first
public class ChargeMoveInfo extends MoveInfo {
    private final int power;
    private final String description;

    public ChargeMoveInfo(String name, int power, String description) {
        super(name);
        this.power = power;
        this.description = description;
    }

    @Override
    public Move createMove() {
        return new ChargeMove(getName(), power, description);
    }
}
