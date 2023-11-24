package com.example.tetris;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Block extends GameController {
    private Color color;
    private boolean fill;
    private double x;
    private double y;
    private double size;
    private double borderSize;

    public Block(double x, double y, double size) {
        color = Color.WHITE;
        fill = false;
        this.x = x;
        this.y = y;
        this.size = size;
        this.borderSize = 2;
    }

    public void render(GraphicsContext gc) {
        if (fill) {
            gc.setFill(color.darker());
            gc.fillRoundRect(x, y, size, size, 4, 4);

            gc.setFill(color);
            gc.fillRoundRect(x + this.borderSize, y + this.borderSize, size - 2 * this.borderSize, size - 2 * this.borderSize, 4, 4);
        }
    }

    public void setData(boolean fill, Color color) {
        this.fill = fill;
        this.color = color;
    }

    public boolean getFill() {
        return fill;
    }

    public Color getColor() {
        return color;
    }

    // 블럭 복사
    public void copyData(Block block) {
        this.fill = block.getFill();
        this.color = block.getColor();
    }
}
