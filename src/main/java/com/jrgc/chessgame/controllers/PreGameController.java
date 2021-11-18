package com.jrgc.chessgame.controllers;

import com.jrgc.chessgame.Settings;
import com.jrgc.chessgame.models.game.Player;
import com.jrgc.chessgame.models.pieces.Piece;
import com.jrgc.chessgame.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PreGameController {

    @FXML
    private Text titleLabel;

    @FXML
    private ChoiceBox<Settings.BoardStyle> boardStyleChoiceBox;

    @FXML
    private ChoiceBox<Settings.PieceStyle> pieceStyleChoiceBox;

    @FXML
    private CheckBox soundCheckBox;

    private final Settings settings = Settings.getInstance();

    public void initialize(){
        titleLabel.setText(String.format("%s vs %s", settings.getName(Player.WHITE), settings.getName(Player.BLACK)));

        pieceStyleChoiceBox.getItems().setAll(Settings.PieceStyle.values());
        pieceStyleChoiceBox.getSelectionModel().select(settings.getPieceStyle());
        pieceStyleChoiceBox.setOnAction(event -> {
            Settings.PieceStyle pieceStyle = pieceStyleChoiceBox.getSelectionModel().getSelectedItem();
            settings.setPieceStyle(pieceStyle);
        });

        boardStyleChoiceBox.getItems().setAll(Settings.BoardStyle.values());
        boardStyleChoiceBox.getSelectionModel().select(settings.getBoardStyle());
        boardStyleChoiceBox.setOnAction(event -> {
            Settings.BoardStyle boardStyle = boardStyleChoiceBox.getSelectionModel().getSelectedItem();
            settings.setBoardStyle(boardStyle);
        });

        soundCheckBox.setOnAction(event -> {
            settings.setSoundOn(soundCheckBox.isSelected());
        });
        soundCheckBox.setSelected(settings.isSoundOn());
    }

    public void onStartClick(){
        settings.exportSettings();
        SceneManager.goToGame(getRoot());
    }

    public void onBackClick() {
        SceneManager.goToPlayers(getRoot());
    }

    public Stage getRoot(){
        return (Stage) titleLabel.getScene().getWindow();
    }

    public void onPawnRuleClick() {
        SceneManager.popUpRules(getRoot(), Piece.PieceType.PAWN);
    }

    public void onBishopRuleClick() {
        SceneManager.popUpRules(getRoot(), Piece.PieceType.BISHOP);
    }

    public void onKnightRuleClick() {
        SceneManager.popUpRules(getRoot(), Piece.PieceType.KNIGHT);
    }

    public void onRookRuleClick() {
        SceneManager.popUpRules(getRoot(), Piece.PieceType.ROOK);
    }

    public void onQueenRuleClick(){ SceneManager.popUpRules(getRoot(), Piece.PieceType.QUEEN); }

    public void onKingRuleClick() {
        SceneManager.popUpRules(getRoot(), Piece.PieceType.KING);
    }
}
