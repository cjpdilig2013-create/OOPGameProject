package com.badlogic.drop;

import com.badlogic.drop.beach_CreatureStatsSystem.Enemy;
import com.badlogic.drop.beach_CreatureStatsSystem.PlayerCreature;
import com.badlogic.drop.cj_BattleSystem.Battle;
import com.badlogic.drop.ot_MoveSkillsSystem.DamageMove;
import com.badlogic.drop.ot_MoveSkillsSystem.DefendMove;
import com.badlogic.drop.ot_MoveSkillsSystem.HealMove;
import com.badlogic.drop.ot_MoveSkillsSystem.Move;
import com.badlogic.drop.toon_BattleScreenSystem.BattleScreen;
import com.badlogic.gdx.Game;

import java.util.ArrayList;
import java.util.List;

public class Main extends Game {

    @Override
    public void create() {
        // 1. สร้างตัวละครจากระบบของ Beach[cite: 9, 10, 11]
        PlayerCreature player = new PlayerCreature("Liquid Cat", 100, 20, 15, 18);
        Enemy enemy = new Enemy("Vacuum Monster", 80, 15, 10, 12);

        // 2. กำหนดรายการท่าโจมตีของผู้เล่นจากระบบของ OT[cite: 14, 15, 16, 17]
        List<Move> playerMoves = new ArrayList<>();
        playerMoves.add(new DamageMove("Splash Hit", 10, "A powerful splash attacks the enemy."));
        playerMoves.add(new DamageMove("Water Claw", 15, "Sharp water claws hit the enemy."));
        playerMoves.add(new DefendMove("Defend", 10, "Reduces damage from the next attack by 10."));
        playerMoves.add(new HealMove("Heal", 30, "Restores 30 HP."));

        // 3. กำหนดรายการท่าโจมตีของศัตรู
        List<Move> enemyMoves = new ArrayList<>();
        enemyMoves.add(new DamageMove("Bite", 20, "The enemy bites the player."));

        // 4. เริ่มต้นระบบการต่อสู้ของ CJ[cite: 6, 7, 8]
        Battle battle = new Battle(player, enemy);

        // 5. สลับไปแสดงผลบนหน้าจอ UI ของตูน
        setScreen(new BattleScreen(battle, playerMoves, enemyMoves));
    }
}
