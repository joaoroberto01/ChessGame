package com.jrgc.chessgame.controllers;

import com.jrgc.chessgame.Settings;
import com.jrgc.chessgame.models.game.Player;
import com.jrgc.chessgame.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PlayersController {

    @FXML
    private Label errorText;

    @FXML
    private TextField whiteTextField, blackTextField;

    private final Settings settings = Settings.getInstance();

    public void initialize(){
        String whitePlayer = settings.getName(Player.WHITE);
        String blackPlayer = settings.getName(Player.BLACK);

        if (whitePlayer != null)
            whiteTextField.setText(whitePlayer);
        if (blackPlayer != null)
            blackTextField.setText(blackPlayer);
    }

    public void onContinueClick(ActionEvent actionEvent) {
        String whitePlayer = whiteTextField.getText().trim();
        String blackPlayer = blackTextField.getText().trim();

        errorText.setManaged(false);

        if (whitePlayer.isEmpty() || blackPlayer.isEmpty()) {
            errorText.setManaged(true);
            return;
        }

        settings.setName(Player.WHITE, whitePlayer);
        settings.setName(Player.BLACK, blackPlayer);

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        SceneManager.goToPreGame(stage);
    }

    public void onClearClick() {
        whiteTextField.setText("");
        blackTextField.setText("");
    }
}
