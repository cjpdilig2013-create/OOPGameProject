package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.drop.ot_MoveSkillsSystem.ChargeMove;
import com.badlogic.drop.ot_MoveSkillsSystem.DamageMove;
import com.badlogic.drop.ot_MoveSkillsSystem.Move;

//Info for one enemy move. We keep the info instead of the Move itself
//because every battle needs a fresh Move object (ChargeMove remembers if it is charged)
public class MoveInfo {
    private final String name;
    private final int power;
    private final String description;
    private final boolean isCharge;

    public MoveInfo(String name, int power, String description, boolean isCharge) {
        this.name = name;
        this.power = power;
        this.description = description;
        this.isCharge = isCharge;
    }

    public String getName() {
        return name;
    }

    //Creates a new Move from this info
    public Move createMove() {
        if (isCharge) {
            return new ChargeMove(name, power, description);
        }
        return new DamageMove(name, power, description);
    }
}
