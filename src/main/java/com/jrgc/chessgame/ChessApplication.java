package com.jrgc.chessgame;

import com.jrgc.chessgame.utils.SceneManager;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ChessApplication extends Application {
    @Override
    public void start(Stage stage) {
        SceneManager.goToPlayers(stage);
        //SceneManager.goToGame(stage);
        //SceneManager.goToSimulateGame(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}