package com.badlogic.drop.toon_BattleScreenSystem;

// --- [Beach] ดึงคลาส Creature สำหรับสเตตัส ---
import com.badlogic.drop.beach_CreatureStatsSystem.Creature;

// --- [CJ] ดึงคลาสทั้งหมดของ CJ มาใช้งานโดยตรง ---
import com.badlogic.drop.cj_BattleSystem.Battle;
import com.badlogic.drop.cj_BattleSystem.BattleResult; // ดึงคลาสเก็บผลการต่อสู้
import com.badlogic.drop.cj_BattleSystem.TurnManager;  // ดึงคลาสจัดการเทิร์น

// --- [OT] ดึงคลาส Move สำหรับระบบสกิล ---
import com.badlogic.drop.ot_MoveSkillsSystem.Move;

// --- LibGDX & Java Utilities ---
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleScreen extends ScreenAdapter implements BattleMenu.Listener {

    private final Battle battle;
    private final List<Move> playerMoves;
    private final List<Move> enemyMoves;

    private final OrthographicCamera camera = new OrthographicCamera();
    private final FitViewport viewport = new FitViewport(800, 480, camera);
    private final ShapeRenderer shape = new ShapeRenderer();
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font = new BitmapFont();
    private final Random random = new Random();

    private final DialogueBox dialogue = new DialogueBox(10, 10, 460, 130);
    private final BattleMenu menu = new BattleMenu(480, 10, 310, 130, this);

    private boolean inMoveMenu = false;

    public BattleScreen(Battle battle, List<Move> playerMoves, List<Move> enemyMoves) {
        this.battle = battle;
        this.playerMoves = playerMoves;
        this.enemyMoves = enemyMoves;
        font.getData().setScale(1.2f);

        // [CJ] เรียกใช้ระบบเริ่มการต่อสู้
        battle.startBattle();
        showMainMenu("Battle start!");
    }

    @Override
    public void show() {
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    // [Beach] ดึงข้อมูลจาก Creature
    private String creatureName(Creature c) { return c.getName(); }
    private int hpOf(Creature c)            { return c.getCurrentHp(); }
    private int maxHpOf(Creature c)         { return c.getMaxHp(); }

    // [OT] ดึงข้อมูลชื่อสกิลจาก Move
    private String moveName(Move m)         { return m.getName(); }

    // ---------------- เมนูและการแสดงผล ----------------

    private void showMainMenu(String message) {
        inMoveMenu = false;
        String question = "What will " + creatureName(battle.getPlayer()) + " do?";
        dialogue.setText(message.isEmpty() ? question : message + "\n" + question);

        List<String> labels = new ArrayList<>();
        labels.add("FIGHT");
        labels.add("BAG");
        labels.add("PARTY");
        labels.add("RUN");
        menu.setButtons(labels);
    }

    private void showMoveMenu() {
        inMoveMenu = true;
        dialogue.setText("Choose a move!");

        List<String> labels = new ArrayList<>();
        for (Move move : playerMoves) {
            labels.add(moveName(move));
        }
        labels.add("BACK");
        menu.setButtons(labels);
    }

    @Override
    public void onButtonClicked(int index) {
        if (battle.isBattleOver()) {
            Gdx.app.exit();
            return;
        }

        if (!inMoveMenu) {
            if (index == 0) {
                showMoveMenu();
            } else if (index == 1) {
                dialogue.setText("Bag: Coming Soon");
            } else if (index == 2) {
                dialogue.setText("Party: Coming Soon");
            } else {
                Gdx.app.exit();
            }
        } else {
            if (index == playerMoves.size()) {
                showMainMenu("");
            } else {
                executeTurn(playerMoves.get(index));
            }
        }
    }

    // ---------------- ลอจิกการเทิร์น ----------------

    private void executeTurn(Move playerMove) {
        // 1. [CJ] โยนสกิลผู้เล่นให้ Battle ประมวลผล
        String playerResult = battle.playerTurn(playerMove);
        String fullMessage = playerResult;

        // 2. [CJ] ดึง TurnManager ออกมาเพื่อเช็กว่าตอนนี้ถึงตาของศัตรูจริงหรือไม่
        TurnManager turnManager = battle.getTurnManager();
        if (!battle.isBattleOver() && turnManager.isEnemyTurn() && !enemyMoves.isEmpty()) {
            Move enemyMove = enemyMoves.get(random.nextInt(enemyMoves.size()));
            String enemyResult = battle.enemyTurn(enemyMove);
            fullMessage += "\n" + enemyResult;
        }

        // 3. [CJ] ถ้าการต่อสู้จบลง ดึงคลาส BattleResult ของ CJ ออกมาใช้งานเพื่อแสดงผลแพ้ชนะ
        if (battle.isBattleOver()) {
            battle.getWinner(); // สั่งให้ Battle คำนวณผู้ชนะและอัปเดตค่าเข้า BattleResult

            BattleResult result = battle.getBattleResult(); // [CJ] ดึงออบเจกต์ BattleResult ออกมาตรงๆ
            dialogue.setText(fullMessage + "\nResult: " + result.getResult()); // แสดงผลลัพธ์ เช่น "Player Wins!"

            List<String> endButtons = new ArrayList<>();
            endButtons.add("EXIT");
            menu.setButtons(endButtons);
        } else {
            showMainMenu(fullMessage);
        }
    }

    // ---------------- การวาดหน้าจอ (Rendering) ----------------

    @Override
    public void render(float delta) {
        if (Gdx.input.justTouched()) {
            Vector2 click = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            menu.handleClick(click.x, click.y);
        }

        // [Beach] & [CJ] ดึง Creature ของผู้เล่นและศัตรูออกมาจาก Battle
        Creature player = battle.getPlayer();
        Creature enemy = battle.getEnemy();

        ScreenUtils.clear(0.8f, 0.92f, 0.8f, 1f);
        viewport.apply();
        camera.update();
        shape.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        shape.begin(ShapeRenderer.ShapeType.Filled);
        drawInfoBox(40, 390, 300, 70);
        drawInfoBox(460, 170, 320, 70);
        dialogue.drawShapes(shape);
        menu.drawShapes(shape);
        shape.end();

        batch.begin();
        font.setColor(Color.BLACK);
        // [Beach] วาดชื่อและค่า HP จากคลาส Creature
        font.draw(batch, creatureName(enemy) + "\nHP " + hpOf(enemy) + " / " + maxHpOf(enemy), 54, 448);
        font.draw(batch, creatureName(player) + "\nHP " + hpOf(player) + " / " + maxHpOf(player), 474, 228);

        dialogue.drawText(batch, font);
        menu.drawText(batch, font);
        batch.end();
    }

    private void drawInfoBox(float x, float y, float w, float h) {
        shape.setColor(Color.DARK_GRAY);
        shape.rect(x, y, w, h);
        shape.setColor(Color.WHITE);
        shape.rect(x + 3, y + 3, w - 6, h - 6);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        shape.dispose();
        batch.dispose();
        font.dispose();
    }
}
