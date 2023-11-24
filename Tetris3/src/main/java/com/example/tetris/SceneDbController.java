package com.example.tetris;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Savepoint;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class SceneDbController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resource) {
    }

    private Stage pop;

    // 메인화면 닫기
    @FXML
    private Button mainCloseBtn;

    public void mainClose() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("게임 종료");
        alert.setHeaderText("정말 종료하시겠습니까?");
        alert.setContentText("");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            pop = (Stage) mainCloseBtn.getScene().getWindow();
            pop.close();
        } else {
            alert.close();
        }
    }


    // 도움말 팝업
    @FXML
    private Button helpPopBtn;
    @FXML
    private Button helpCloseBtn;

    public void helpPopup() {
        Stage mainStage = (Stage) helpPopBtn.getScene().getWindow();

        pop = new Stage(StageStyle.DECORATED);
        pop.initModality(Modality.WINDOW_MODAL);
        pop.initOwner(mainStage);

        try {
            Parent nextScene = FXMLLoader.load(getClass().getResource("help.fxml"));

            Scene scene = new Scene(nextScene);
            pop.setScene(scene);
            pop.setTitle("Help");
            pop.setResizable(false);
            pop.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 도움말 팝업 닫기
    public void helpClose() {
        pop = (Stage) helpCloseBtn.getScene().getWindow();
        pop.close();
    }


    // 회원가입 팝업
    @FXML
    private Button signPopBtn;
    @FXML
    private Button signCloseBtn;

    public void signPopup() {
        Stage mainStage = (Stage) signPopBtn.getScene().getWindow();

        pop = new Stage(StageStyle.DECORATED);
        pop.initModality(Modality.WINDOW_MODAL);
        pop.initOwner(mainStage);

        try {
            Parent nextScene = FXMLLoader.load(getClass().getResource("signUp.fxml"));

            Scene scene = new Scene(nextScene);
            pop.setScene(scene);
            pop.setTitle("sign-up");
            pop.setResizable(false);
            pop.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 회원가입 팝업 닫기
    public void signClose() {
        pop = (Stage) signCloseBtn.getScene().getWindow();
        pop.close();
    }


    // 회원가입
    @FXML
    private TextField id;
    @FXML
    private TextField pw;
    @FXML
    private TextField nickname;
    @FXML
    private Button joinButton;

    public void insertMember() {
        DBUtil db = new DBUtil();
        Connection conn = db.getConnection();

        PreparedStatement pstmt = null;
        String sql = "INSERT INTO users(id, pw, nickname) VALUES(?,?,?)";

        String signId = id.getText();
        String signPw = pw.getText();
        String signName = nickname.getText();

        if (signId.equals("") || signPw.equals("") || signName.equals("")) {
            // 만약 아이디, 비번, 이름란 중 하나라도 공백이라면 공백으로 인한 실패창 생성

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("경고창");
            alert.setHeaderText("경고!");
            alert.setContentText("아이디, 비밀번호, 이름 중 공백인 란이 존재합니다.");

            alert.showAndWait();
        } else {
            try {
                // 회원 정보 insert 문에 삽입 및 sql 문 실행 => 회원가입
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, id.getText());
                pstmt.setString(2, pw.getText());
                pstmt.setString(3, nickname.getText());

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("회원가입");
                alert.setHeaderText("회원가입 하시겠습니까?");
                alert.setContentText("");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    pstmt.executeUpdate();
                    System.out.println("회원가입 완료");

                    Alert alert1 = new Alert(Alert.AlertType.WARNING);
                    alert1.setTitle("회원가입");
                    alert1.setHeaderText("회원가입 완료");
                    alert1.setContentText("메인화면으로 돌아갑니다");

                    alert1.showAndWait();

                    signClose();
                    // 아이디 비번 이름 입력란 초기화
                    id.setText("");
                    pw.setText("");
                    nickname.setText("");
                } else {
                    alert.close();
                }

            } catch (Exception e) {
                // 회원가입 실패창 생성
                System.out.println("아이디가 중복되거나, 아이디, 비밀번호, 이름의 길이가 초과됩니다.");

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("경고창");
                alert.setHeaderText("경고!");
                alert.setContentText("아이디가 중복 혹은 \n" +
                        "아이디 혹은 비밀번호가 15자 초과 혹은 닉네임이 8자 초과입니다");

                alert.showAndWait();
            }
        }
    }

    // 로그인
    @FXML
    private Button loginButton;
    String loginPw = null;
    String windowPw = null;

    public void login() {
        DBUtil db = new DBUtil();
        Connection conn = db.getConnection();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT pw FROM users WHERE id = ?";

        // 서버에 저장된 아이디에 해당되는 비밀번호를 가져옴
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 해당 비밀번호를 loginPw 라는 변수에 저장
        try {
            rs = pstmt.executeQuery();
            rs.next();
            loginPw = rs.getString("pw");
            System.out.println("입력하신 아이디에 해당하는 패스워드 : " + loginPw);

            // 로그인 화면의 pw 입력란의 pw 값과 위에서 가져온 비밀번호 값을 대조하여 로그인 가능 여부 확인
            try {
                windowPw = pw.getText();
                System.out.println("비밀번호 입력란에 입력하신 패스워드 : " + windowPw);

                // 입력란에 입력한 pw와 DB에 저장된 pw가 일치하다면
                if (windowPw.equals(loginPw)) {
                    System.out.println("입력하신 아이디와 비밀번호가 일치합니다 \n" +
                            "--------------------------------------------------------");

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("로그인");
                    alert.setHeaderText("정말 로그인 하시겠습니까?");
                    alert.setContentText("로그인 시 게임화면으로 이동합니다.");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        try {
                            System.out.println("게임이 실행됩니다... \n" +
                                    "--------------------------------------------------------");

                            // 아이디 비번 입력란 초기화
                            id.setText("");
                            pw.setText("");

                            Parent nextScene = FXMLLoader.load(getClass().getResource("game.fxml"));
                            Scene scene = new Scene(nextScene);
                            Stage primaryStage = (Stage) loginButton.getScene().getWindow();
                            primaryStage.setScene(scene);

                            scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                                if (RunGame.game != null) {
                                    RunGame.game.keyHandler(e);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        alert.close();
                    }
                } else {
                    // 그렇지 않다면 경고창
                    System.out.println("입력하신 아이디와 비밀번호가 일치하지 않습니다. \n" +
                            "--------------------------------------------------------");

                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("경고창");
                    alert.setHeaderText("경고!");
                    alert.setContentText("입력하신 아이디와 비밀번호가 일치하지 않습니다");

                    alert.showAndWait();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            // 아이디가 존재하지 않을 경우 경고창
            System.out.println("해당 아이디는 존재하지 않습니다. \n" +
                    "--------------------------------------------------------");

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("경고창");
            alert.setHeaderText("경고!");
            alert.setContentText("해당 아이디는 존재하지 않습니다.");

            alert.showAndWait();
        }
    }
}