module com.example.tetris {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.tetris to javafx.fxml;
    exports com.example.tetris;
}