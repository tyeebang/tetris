package com.example.tetris;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class App extends Application {
    public static App app; //싱글톤 기법을 활용하기 위한 스태틱 변수
    public Game game = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            app = this; //스태틱 변수에 자기자신을 넣어서 싱글톤으로 활용함.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("main.fxml"));
            AnchorPane anchorPane = (AnchorPane)loader.load();
            primaryStage.setTitle("Tetris!");
            Scene scene = new Scene(anchorPane);

            scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                if(game != null)
                    game.keyHandler(e); //게임의 이벤트 핸들러로 넘겨준다.
            });
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
