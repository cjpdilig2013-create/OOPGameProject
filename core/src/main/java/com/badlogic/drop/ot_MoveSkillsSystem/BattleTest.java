package com.badlogic.drop.ot_MoveSkillsSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Enemy;
import com.badlogic.drop.beach_CreatureStatsSystem.PlayerCreature;
import com.badlogic.drop.cj_BattleSystem.Battle;

import java.util.Scanner;

public class BattleTest {

    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        PlayerCreature player = new PlayerCreature(
            "Liquid Cat",
            100,
            20,
            15,
            18
        );

        Enemy enemy = new Enemy(
            "Vacuum Monster",
            80,
            15,
            10,
            12
        );

        // Moves available for the player to choose
        Move splashHit = new DamageMove(
            "Splash Hit",
            10,
            "A powerful splash attacks the enemy."
        );

        Move waterClaw = new DamageMove(
            "Water Claw",
            15,
            "Sharp water claws hit the enemy."
        );

        Move defend = new DefendMove(
            "Defend",
            10,
            "Reduces damage from the next attack by 10."
        );
        Move heal = new HealMove(
            "Heal",
            30,
            "Restores 30 HP."
        );

        // Enemy move
        Move bite = new DamageMove(
            "Bite",
            20,
            "The enemy bites the player."
        );

        Battle battle = new Battle(player, enemy);
        battle.startBattle();

        System.out.println("=== BATTLE START ===");

        while (battle.isBattleInProgress()) {
            System.out.println("\n" + player.getName()
                + " HP: " + player.getCurrentHp() + "/" + player.getMaxHp());

            System.out.println(enemy.getName()
                + " HP: " + enemy.getCurrentHp() + "/" + enemy.getMaxHp());

            System.out.println("\nChoose a move:");
            System.out.println("1. " + splashHit.getName()
                + " (Power: " + splashHit.getPower() + ")");
            System.out.println("2. " + waterClaw.getName()
                + " (Power: " + waterClaw.getPower() + ")");
            System.out.println("3. " + defend.getName()
                + " (increst defend by " + defend.getPower() + ")");
            System.out.println("4. " + heal.getName()
                + " (Heal: " + heal.getPower() + ")");

            System.out.print("Enter 1, 2, or 3, 4 : ");

            int choice = scanner.nextInt();

            Move selectedMove;

            if (choice == 1) {
                selectedMove = splashHit;
            } else if (choice == 2) {
                selectedMove = waterClaw;
            } else if (choice == 3) {
                selectedMove = defend;
            } else if (choice == 4) {
                selectedMove = heal;}
            else {
                System.out.println("Invalid choice. Please choose 1, 2, or 3.");
                continue;
            }

            // Player uses the selected move
            System.out.println(GREEN + battle.playerTurn(selectedMove) + RESET);

            if (!battle.isBattleInProgress()) {
                break;
            }

            // Enemy uses Bite
            System.out.println(RED + battle.enemyTurn(bite) + RESET);
        } // End of battle loop

        System.out.println("\n=== BATTLE OVER ===");
        System.out.println("Winner: " + battle.getWinner());

        scanner.close();
    }
}
