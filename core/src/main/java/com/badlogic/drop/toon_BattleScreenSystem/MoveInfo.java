package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.drop.ot_MoveSkillsSystem.Move;

//Info for one enemy move. We keep the info instead of the Move itself
//because every battle needs a fresh Move object (ChargeMove and MathMove remember if they were used)
//Each kind of move has its own subclass (DamageMoveInfo, ChargeMoveInfo, ...) that knows how to create its Move
//To add a new kind of move, just make a new subclass. Nothing else needs to change (Open/Closed Principle)
public abstract class MoveInfo {
    private final String name;

    public MoveInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    //Creates a new Move from this info (every subclass does this in its own way)
    public abstract Move createMove();
}
