package com.jrgc.chessgame.controllers;

import com.jrgc.chessgame.models.pieces.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.text.Text;

public class RulesController {

    @FXML
    private Text titleLabel, rulesLabel;

    public void setup(Piece.PieceType pieceType){
        titleLabel.setText(pieceType.toFormattedString());
        rulesLabel.setText(pieceType.getPieceRule());
    }

    public void onCloseClick(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }
}
