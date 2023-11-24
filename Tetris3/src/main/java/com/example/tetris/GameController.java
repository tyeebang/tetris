package com.example.tetris;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;

public class GameController {
    @FXML
    private Canvas gameCanvas;
    @FXML
    protected Label scoreText;

    @FXML
    public void initialize() {
        System.out.println("메인 화면 초기화");
        RunGame.runGame.game = new Game(gameCanvas);
    }
}
