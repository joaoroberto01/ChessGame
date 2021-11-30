package com.jrgc.chessgame.controllers;

import com.jrgc.chessgame.Settings;
import com.jrgc.chessgame.models.game.DrawType;
import com.jrgc.chessgame.models.game.GameOverReason;
import com.jrgc.chessgame.models.game.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.text.Text;

public class GameOverController {

    @FXML
    private Text gameOverLabel;

    public void setup(Player winner, GameOverReason gameOverReason, DrawType drawType){
        String winnerString = "Empate por ";
        if (winner != null)
            winnerString = Settings.getInstance().getName(winner) + " venceu por ";

        winnerString += winner == null && gameOverReason == null ? drawType : gameOverReason;

        gameOverLabel.setText(winnerString);
    }

    public void setupSimulation(Player winner, String whiteName, String blackName){
        String winnerString = "Empate";
        if (winner != null) {
            winnerString = winner == Player.WHITE ? whiteName : blackName;
            winnerString += " venceu";
        }

        gameOverLabel.setText(winnerString);
    }

    public void onCloseClick(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }
}
