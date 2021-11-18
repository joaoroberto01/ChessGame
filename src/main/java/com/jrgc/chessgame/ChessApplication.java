package com.jrgc.chessgame;

import com.jrgc.chessgame.utils.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class ChessApplication extends Application {
    @Override
    public void start(Stage stage) {
        SceneManager.goToPlayers(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}