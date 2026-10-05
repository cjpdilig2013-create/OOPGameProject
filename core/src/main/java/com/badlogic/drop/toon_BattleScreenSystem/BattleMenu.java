package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

//BattleMenu = the buttons at the bottom-right of the battle screen
//Example: FIGHT  BAG  PARTY  RUN
//It only draws buttons and tells the listener which button number was clicked.
//(BattleScreen decides what that number means)
public class BattleMenu {

    public interface Listener {
        void onButtonClicked(int index);   // 0 = first button, 1 = second button, ...
    }

    private final float x, y, width, height;
    private final Listener listener;

    private final List<String> labels = new ArrayList<>();
    private final List<Rectangle> rects = new ArrayList<>();
    private final GlyphLayout layout = new GlyphLayout();

    public BattleMenu(float x, float y, float width, float height, Listener listener) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.listener = listener;
    }

    //Replace the buttons with new ones (laid out in 2 columns)
    public void setButtons(List<String> newLabels) {
        labels.clear();
        rects.clear();
        labels.addAll(newLabels);

        float gap = 8f;
        int rows = (labels.size() + 1) / 2;
        float buttonWidth = (width - gap * 3) / 2f;
        float buttonHeight = (height - gap * (rows + 1)) / rows;

        for (int i = 0; i < labels.size(); i++) {
            int col = i % 2;
            int row = i / 2;
            float bx = x + gap + col * (buttonWidth + gap);
            float by = y + height - gap - (row + 1) * buttonHeight - row * gap;
            rects.add(new Rectangle(bx, by, buttonWidth, buttonHeight));
        }
    }

    //Remove all buttons (used when the battle is over)
    public void hide() {
        labels.clear();
        rects.clear();
    }

    public void handleClick(float clickX, float clickY) {
        for (int i = 0; i < rects.size(); i++) {
            if (rects.get(i).contains(clickX, clickY)) {
                listener.onButtonClicked(i);
                return;
            }
        }
    }

    //Draw the buttons (call between shape.begin() and shape.end())
    public void drawShapes(ShapeRenderer shape) {
        for (Rectangle r : rects) {
            shape.setColor(Color.DARK_GRAY);
            shape.rect(r.x, r.y, r.width, r.height);
            shape.setColor(Color.LIGHT_GRAY);
            shape.rect(r.x + 3, r.y + 3, r.width - 6, r.height - 6);
        }
    }

    //Draw the button names (call between batch.begin() and batch.end())
    public void drawText(SpriteBatch batch, BitmapFont font) {
        font.setColor(Color.BLACK);
        for (int i = 0; i < rects.size(); i++) {
            Rectangle r = rects.get(i);
            layout.setText(font, labels.get(i));
            font.draw(batch, labels.get(i),
                r.x + (r.width - layout.width) / 2f,
                r.y + (r.height + layout.height) / 2f);
        }
    }
}
