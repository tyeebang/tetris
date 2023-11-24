package com.example.tetris;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.util.Random;

public class Player extends GameController {
    private Point2D[][][] shape = new Point2D[7][][]; // 테트리스의 모든 블럭이 있는 3차원 배열
    // [7개 종류의 모양] [각 블럭이 가진 모양의 개수] [블럭의 위치]

    private int current = 0; // 현재의 블럭 모양
    private int rotate = 0; // 회전 상태
    private int nowColor = 0; // 현재의 색상
    private Random rnd;
    private Color[] colorSet = new Color[7]; // 7개의 색상을 무작위로 부여

    // 유저가 조종하는 블럭의 x, y 위치
    private int x = 5;
    private int y = 2;

    private Block[][] board;

    // 생성자
    public Player(Block[][] board) {
        this.board = board;

        // ㄴ 모양
        shape[0] = new Point2D[4][]; // 돌렸을 때 모양이 4개
        shape[0][0] = getPointArray("0,-2:0,-1:0,0:1,0");
        shape[0][1] = getPointArray("0,1:0,0:1,0:2,0");
        shape[0][2] = getPointArray("-1,0:0,0:0,1:0,2");
        shape[0][3] = getPointArray("-2,0:-1,0:0,0:0,-1");

        // ㄴ 반전 모양
        shape[1] = new Point2D[4][]; // 돌렸을 때 모양이 4개
        shape[1][0] = getPointArray("0,-2:0,-1:0,0:-1,0");
        shape[1][1] = getPointArray("0,-1:0,0:1,0:2,0");
        shape[1][2] = getPointArray("0,0:1,0:0,1:0,2");
        shape[1][3] = getPointArray("-2,0:-1,0:0,0:0,1");

        // ㄹ 모양
        shape[2] = new Point2D[2][]; // 돌렸을 때 모양이 2개
        shape[2][0] = getPointArray("-1,1:0,1:0,0:1,0");
        shape[2][1] = getPointArray("0,1:-1,0:0,0:-1,-1");

        // ㄹ 반전 모양
        shape[3] = new Point2D[2][]; // 돌렸을 때 모양이 2개
        shape[3][0] = getPointArray("0,1:1,1:-1,0:0,0");
        shape[3][1] = getPointArray("-1,1:-1,0:0,0:0,-1");

        // ㅁ 모양
        shape[4] = new Point2D[1][]; // 돌렸을 때 모양이 1개
        shape[4][0] = getPointArray("0,0:1,0:0,1:1,1");
        // ㅗ 모양
        shape[5] = new Point2D[4][]; // 돌렸을 때 모양이 4개
        shape[5][0] = getPointArray("-1,0:0,0:1,0:0,-1");
        shape[5][1] = getPointArray("0,1:0,0:1,0:0,-1");
        shape[5][2] = getPointArray("0,1:-1,0:0,0:1,0");
        shape[5][3] = getPointArray("0,1:-1,0:0,0:0,-1");

        // ㅣ 모양
        shape[6] = new Point2D[2][]; // 돌렸을 때 모양이 2개
        shape[6][0] = getPointArray("0,-1:0,0:0,1:0,2");
        shape[6][1] = getPointArray("-1,0:0,0:1,0:2,0");

        // 색상 부여
        colorSet[0] = Color.RED;
        colorSet[1] = Color.ORANGE;
        colorSet[2] = Color.YELLOW;
        colorSet[3] = Color.GREEN;
        colorSet[4] = Color.BLUE;
        colorSet[5] = Color.NAVY;
        colorSet[6] = Color.PURPLE;

        rnd = new Random();
        current = rnd.nextInt(shape.length);
        nowColor = rnd.nextInt(colorSet.length);

        draw(false);
    }

    private void draw(boolean remove) {
        // 블럭을 판에서 표시해주거나 없애주는 매서드
        for(int i = 0; i < shape[current][rotate].length; i++) {
            int bx = (int)shape[current][rotate][i].getX() + x;
            int by = (int)shape[current][rotate][i].getY() + y;
            board[by][bx].setData(!remove, colorSet[nowColor]); //제거나 색칠이냐
        }
    }

    public Point2D[] getPointArray(String pointStr) {
        // 데이터가 들어오면 해당 데이터를 Point 배열로 변경해주는 역할
        Point2D[] arr = new Point2D[4];
        String[] pointList = pointStr.split(":");
        for (int i = 0; i < pointList.length; i++) {
            String[] point = pointList[i].split(",");
            double x = Double.parseDouble(point[0]);
            double y = Double.parseDouble(point[1]);
            arr[i] = new Point2D(x, y);
        }
        return arr;
    }

    public void keyHandler(KeyEvent e) {
        // 유저의 키보드 입력을 처리하는 메서드
        int dx = 0, dy = 0;
        boolean rot = false;
        // 오른쪽 왼쪽 이동 혹은 회전
        if (e.getCode() == KeyCode.LEFT) {
            dx -= 1;
        } else if (e.getCode() == KeyCode.RIGHT) {
            dx += 1;
        } else if (e.getCode() == KeyCode.UP) {
            rot = true;
        }

        move(dx, dy, rot); // 이동

        // 아래로 이동
        if (e.getCode() == KeyCode.DOWN) {
            down();
        } else if (e.getCode() == KeyCode.SPACE) {
            while (!down()) {

            }
        }
    }

    private void move(int dx, int dy, boolean rot) {
        // 블럭을 이동하는 메서드
        draw(true); // 지우고
        x += dx;
        y += dy;
        if (rot) {
            rotate = (rotate + 1) % shape[current].length; // 모양 개수만큼 증가
        }
        if (!checkPossible()) {
            x -= dx;
            y -= dy;
            // 회전되었다면 회전 원상복구
            if (rot) {
                rotate = rotate - 1 < 0 ? shape[current].length - 1 : rotate - 1;
            }
        }
        draw(false);
    }

    public boolean down() {
        // 블럭을 한칸 내리는 메서드
        draw(true); // 지우기
        y += 1;
        if (!checkPossible()) {
            y -= 1;
            draw(false); // 내려놓은 블럭 다시 그리기
            RunGame.runGame.game.checkLineStatus(); // 블럭을 내린 후 라인 상태를 확인
            getNextBlock();
            draw(false); // 이동 후 다시 그리기
            return true;
        }
        draw(false); // 이동 후 다시 그리기
        return false;
    }

    private void getNextBlock() {
        // 새로운 블럭과 색상을 랜덤하게 뽑고, x, y값과 회전 상태를 초기화 시켜주는 역할
        current = rnd.nextInt(shape.length);
        nowColor = rnd.nextInt(colorSet.length);
        x = 5;
        y = 2;
        rotate = 0;
    }

    private boolean checkPossible() {
        // 블럭의 이동이 가능한지 체크하는 메서드
        for (int i = 0; i < shape[current][rotate].length; i++) {
            int bx = (int)shape[current][rotate][i].getX() + x;
            int by = (int)shape[current][rotate][i].getY() + y;
            if (bx < 0 || by < 0 || bx >= 10 || by >= 20) {
                return false;
            }
            // 만약 그곳에 블럭이 이미 존재한다면
            if (board[by][bx].getFill()) {
                return false;
            }
        }
        return true;
    }
}
