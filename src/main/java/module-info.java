module com.jrgc.chessgame {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.jrgc.chessgame to javafx.fxml;
    exports com.jrgc.chessgame;
    exports com.jrgc.chessgame.models;
    opens com.jrgc.chessgame.models to javafx.fxml;
    exports com.jrgc.chessgame.models.pieces;
    opens com.jrgc.chessgame.models.pieces to javafx.fxml;
    exports com.jrgc.chessgame.controllers;
    opens com.jrgc.chessgame.controllers to javafx.fxml;
}