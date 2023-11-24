package com.example.tetris;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class Game extends GameController {
    // Canvas 의 그리기 단계는 GraphicsContext 가져오기, 어떻게 그릴지 설정, 그리는 메서드 호출
    private GraphicsContext gc;
    public Block[][] board; // 2차원의 배열이자 테트리스의 스테이지

    private double width;
    private double height;

    private AnimationTimer mainLoop; // 쓰레드로 update render 메서드를 담당
    private long before; // 현재 프레임이 이전 프레임 실행 후 몇 초후 실행되었는지 저장하는 변수

    private Player player;
    private double blockDownTime = 0; // 블록이 자동으로 내려오는 시간, 게임이 지속될 수록 속도를 조정해줌

    private int score = 0;

    public Game(Canvas canvas) {
        // 캔버스의 가로, 세로 값 가져오기
        width = canvas.getWidth();
        height = canvas.getHeight();

        double size = (width - 4) / 10;

        board = new Block[20][10]; // 게임판 생성

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                board[i][j] = new Block(j * size + 2, i * size + 2, size);
            }
        }

        this.gc = canvas.getGraphicsContext2D();

        mainLoop = new AnimationTimer() {
            @Override
            public void handle(long nowSec) { // nowSec 는 ns(1/10억 초)단위로 들어옴
                update((nowSec - before) / 1000000000d);
                before = nowSec;
                render();
            }
        };

        before = System.nanoTime();

        // 모양 설정
        player = new Player(board);
        mainLoop.start();
    }

    public void update(double delta) {
        // 1 프레임마다 실행되는 update 매서드 블럭의 자동하강을 담당
        blockDownTime += delta; // 0.5초 마다 블럭을 아래로 내림, 이 수치로 난이도 조절 가능
        if (blockDownTime >= 1) {
            player.down();
            blockDownTime = 0;
        }
    }

    public void checkLineStatus() {
        // 라인의 상태를 확인해주는 역할을 담당
        for (int i = 19; i >= 0; i--) { // 맨 아래부터 검사하면서 한줄씩 올라가기
            boolean clear = true;
            for (int j = 0; j < 10; j++) {
                if (!board[i][j].getFill()) {
                    clear = false; // 한칸이라도 비어 있으면 반복문 나가기
                    break;
                }
            }
            if (clear) { // 줄이 꽉 차 있다면
                score++;
                System.out.println(score + " 점");
                for (int j = 0; j < 10; j++) {
                    board[i][j].setData(false, Color.WHITE); // 해당 줄 클리어
                }
                // 그 위의 쌓인 블럭들 한칸씩 모두 내리기
                for (int j = i - 1; j >= 0; j--) {
                    for (int k = 0; k < 10; k++) {
                        board[j + 1][k].copyData(board[j][k]);
                    }
                }
                // 첫번째 줄 비우기
                for (int j = 0; j < 10; j++) {
                    board[0][j].setData(false, Color.WHITE);
                }
                i++; // 줄을 한번더 검사하기 위해 i값 증가 시키기
            }
        }
    }

    public void render() { // 프레임마다 화면을 그려주는 매서드, block 배열을 반복문으로 돌아다니며 화면을 그려줌
        // 스테이지 그리기
        gc.clearRect(0, 0, width, height); // 모두 지우고 새로 그리기
        gc.setStroke(Color.rgb(255, 255, 255)); // 검은색으로 윤곽선 그리기
        gc.setLineWidth(3);
        gc.strokeRect(0, 0, width, height);

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                board[i][j].render(gc);
            }
        }
    }

    public void keyHandler(KeyEvent e) {
        player.keyHandler(e); // 키보드 핸들링을 담당
    }
}
