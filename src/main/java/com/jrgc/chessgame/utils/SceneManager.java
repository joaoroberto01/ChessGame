package com.jrgc.chessgame.utils;

import com.jrgc.chessgame.ChessApplication;
import com.jrgc.chessgame.Settings;
import com.jrgc.chessgame.controllers.ConfirmationController;
import com.jrgc.chessgame.controllers.GameOverController;
import com.jrgc.chessgame.controllers.PawnPromotionController;
import com.jrgc.chessgame.controllers.RulesController;
import com.jrgc.chessgame.interfaces.ConfirmationListener;
import com.jrgc.chessgame.interfaces.PawnPromotionListener;
import com.jrgc.chessgame.models.game.DrawType;
import com.jrgc.chessgame.models.game.GameOverReason;
import com.jrgc.chessgame.models.game.Player;
import com.jrgc.chessgame.models.pieces.Piece;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SceneManager {

    private static class SceneDetails {
        public final String path;
        public final String title;
        public int width, height;
        public double windowX = -1, windowY = -1;

        public SceneDetails(String path, String title, int width, int height) {
            this.path = path;
            this.title = title;
            this.width = width;
            this.height = height;
        }
    }

    public static void goToPlayers(Stage stage){
        SceneDetails sceneDetails = new SceneDetails("players-view.fxml", "Seleção de Jogadores", 300, 240);

        showScene(stage, sceneDetails);
    }

    public static void goToPreGame(Stage stage){
        SceneDetails sceneDetails = new SceneDetails("pre-game-view.fxml", "Sua partida vai começar em breve!", 700, 500);

        showScene(stage, sceneDetails);
    }

    public static void goToSimulateGame(Stage stage){
        SceneDetails sceneDetails = new SceneDetails("simulate-game-view.fxml", "Simulação de Partida", 1000, 720);

        showScene(stage, sceneDetails);
    }

    public static void goToGame(Stage stage){
        Settings settings = Settings.getInstance();
        String title = settings.getName(Player.WHITE) + " vs " + settings.getName(Player.BLACK);
        SceneDetails sceneDetails = new SceneDetails("game-view.fxml", title, 1000, 720);

        showScene(stage, sceneDetails);
    }

    public static void popUpPawnPromotion(Stage root, double x, double y, Player currentPlayer, PawnPromotionListener pawnPromotionListener) {
        SceneDetails sceneDetails = new SceneDetails("pawn-promotion-view.fxml", "Promoção do Peão", 340, 160);
        sceneDetails.windowX = x;
        sceneDetails.windowY = y;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ChessApplication.class.getResource(sceneDetails.path));
            Parent parent = fxmlLoader.load();
            PawnPromotionController pawnPromotionController = fxmlLoader.getController();
            pawnPromotionController.setup(currentPlayer, pawnPromotionListener);

            showPopUpStage(root, parent, sceneDetails, true, true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void popUpConfirmation(Stage root, String title, ConfirmationListener confirmationListener){
        SceneDetails sceneDetails = new SceneDetails("confirmation-view.fxml", title, 340, 160);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ChessApplication.class.getResource(sceneDetails.path));
            Parent parent = fxmlLoader.load();
            ConfirmationController confirmationController = fxmlLoader.getController();
            confirmationController.setup(sceneDetails.title, confirmationListener);

            showPopUpStage(root, parent, sceneDetails, true, true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void popUpGameOver(Stage root, Player winner, GameOverReason gameOverReason, DrawType drawType) {
        Settings settings = Settings.getInstance();
        String title = winner != null ? settings.getName(winner) + " venceu" : "Empate";

        SceneDetails sceneDetails = new SceneDetails("gameover-view.fxml", title, 340, 160);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ChessApplication.class.getResource(sceneDetails.path));
            Parent parent = fxmlLoader.load();
            GameOverController gameOverController = fxmlLoader.getController();
            gameOverController.setup(winner, gameOverReason, drawType);

            showPopUpStage(root, parent, sceneDetails);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void popUpSimulationGameOver(Stage root, Player winner, String whiteName, String blackName) {
        String title = "Empate";
        if (winner != null) {
            title = winner == Player.WHITE ? whiteName : blackName;
            title += " venceu";
        }

        SceneDetails sceneDetails = new SceneDetails("gameover-view.fxml", title, 340, 160);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ChessApplication.class.getResource(sceneDetails.path));
            Parent parent = fxmlLoader.load();
            GameOverController gameOverController = fxmlLoader.getController();
            gameOverController.setupSimulation(winner, whiteName, blackName);

            showPopUpStage(root, parent, sceneDetails);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void popUpRules(Stage root, Piece.PieceType pieceType){
        SceneDetails sceneDetails = new SceneDetails("rules-view.fxml", "", 650, 300);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ChessApplication.class.getResource(sceneDetails.path));
            Parent parent = fxmlLoader.load();
            RulesController rulesController = fxmlLoader.getController();
            rulesController.setup(pieceType);

            showPopUpStage(root, parent, sceneDetails, false, true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void showScene(Stage stage, SceneDetails sceneDetails){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ChessApplication.class.getResource(sceneDetails.path));

            Scene scene = new Scene(fxmlLoader.load(), sceneDetails.width, sceneDetails.height);

            stage.setTitle(sceneDetails.title);
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void showPopUpStage(Stage root, Parent parent, SceneDetails sceneDetails){
        Scene scene = new Scene(parent, sceneDetails.width, sceneDetails.height);

        Stage stage = new Stage();
        stage.setTitle(sceneDetails.title);
        stage.initOwner(root);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setScene(scene);

        stage.show();
    }

    private static void showPopUpStage(Stage root, Parent parent, SceneDetails sceneDetails, boolean blur, boolean wait){
        Scene scene = new Scene(parent, sceneDetails.width, sceneDetails.height);

        Stage stage = new Stage();
        stage.setTitle(sceneDetails.title);
        if (sceneDetails.windowX != -1)
            stage.setX(sceneDetails.windowX);
        if (sceneDetails.windowY != -1)
            stage.setY(sceneDetails.windowY);

        if (sceneDetails.windowX == -1 || sceneDetails.windowY == -1)
            stage.centerOnScreen();

        stage.initOwner(root);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.setScene(scene);

        root.getScene().getRoot().setEffect(blur ? new BoxBlur(10,10, 2) : null);
        stage.showAndWait();
        root.getScene().getRoot().setEffect(null);
    }
}
