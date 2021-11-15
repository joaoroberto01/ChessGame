package com.jrgc.chessgame.utils;

import com.jrgc.chessgame.ChessApplication;
import com.jrgc.chessgame.controllers.ConfirmationController;
import com.jrgc.chessgame.controllers.PawnPromotionController;
import com.jrgc.chessgame.interfaces.ConfirmationListener;
import com.jrgc.chessgame.interfaces.PawnPromotionListener;
import com.jrgc.chessgame.models.game.Player;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    public static Stage getCurrentStage(Node node){
        return (Stage) node.getScene().getWindow();
    }

    public static void goToLogin(Stage stage){
        SceneDetails sceneDetails = new SceneDetails("login-view.fxml", "Autenticação BankNu", 380, 320);

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

            showPopUpStage(root, parent, sceneDetails);
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

            showPopUpStage(root, parent, sceneDetails);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void showScene(Stage stage, SceneDetails sceneDetails){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ChessApplication.class.getResource(sceneDetails.path));

            Scene scene = new Scene(fxmlLoader.load(), sceneDetails.width, sceneDetails.height);

            stage.setTitle(sceneDetails.title);
            //stage.getIcons().add(new Image(ChessApplication.class.getResourceAsStream("appicon.png")));
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
        if (sceneDetails.windowX != -1)
            stage.setX(sceneDetails.windowX);
        if (sceneDetails.windowY != -1)
            stage.setY(sceneDetails.windowY);

        stage.initOwner(root);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
