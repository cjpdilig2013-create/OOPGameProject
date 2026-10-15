package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.drop.ot_MoveSkillsSystem.MindLockMove;
import com.badlogic.drop.ot_MoveSkillsSystem.Move;

//Info for the Mind Lock move (seals the player's NOTE)
public class MindLockMoveInfo extends MoveInfo {
    private final int power;
    private final String description;

    public MindLockMoveInfo(String name, int power, String description) {
        super(name);
        this.power = power;
        this.description = description;
    }

    @Override
    public Move createMove() {
        return new MindLockMove(getName(), power, description);
    }
}
