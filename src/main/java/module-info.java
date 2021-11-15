module com.jrgc.chessgame {
    requires javafx.controls;
    requires javafx.media;
    requires javafx.fxml;


    opens com.jrgc.chessgame to javafx.fxml;
    exports com.jrgc.chessgame;
    exports com.jrgc.chessgame.models;
    opens com.jrgc.chessgame.models to javafx.fxml;
    exports com.jrgc.chessgame.models.pieces;
    opens com.jrgc.chessgame.models.pieces to javafx.fxml;
    exports com.jrgc.chessgame.controllers;
    opens com.jrgc.chessgame.controllers to javafx.fxml;
    exports com.jrgc.chessgame.interfaces;
    opens com.jrgc.chessgame.interfaces to javafx.fxml;
    exports com.jrgc.chessgame.utils;
    opens com.jrgc.chessgame.utils to javafx.fxml;
    exports com.jrgc.chessgame.models.game;
    opens com.jrgc.chessgame.models.game to javafx.fxml;
}