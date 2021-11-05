package com.jrgc.chessgame.controllers;

import com.jrgc.chessgame.BoardUtils;
import com.jrgc.chessgame.models.BoardPosition;
import com.jrgc.chessgame.models.GameTurnLog;
import com.jrgc.chessgame.models.MoveEvent;
import com.jrgc.chessgame.models.Player;
import com.jrgc.chessgame.models.pieces.Piece;
import com.jrgc.chessgame.models.pieces.Validator;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import static com.jrgc.chessgame.BoardUtils.BOARD_SIZE;

public class GameController implements EventHandler<MouseEvent> {

    @FXML
    private Label turnLabel;

    @FXML
    public GridPane boardGridView;

    private BoardPosition sourcePosition = null, destinationPosition = null;
    private Button lastClickedButton;
    private final GameStateController gameState = GameStateController.getInstance(true);

    public void initialize() {
        setupGrid();
    }

    private void setupGrid() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                BoardPosition currentPosition = new BoardPosition(i, j);
                Piece piece = Piece.create(i, j);
                gameState.setPiece(piece, currentPosition);

                if (piece != null)
                    gameState.getPlayerPieces(piece.getPlayer()).add(piece);

                ImageView pieceImageView = new ImageView();
                pieceImageView.setFitHeight(48);
                pieceImageView.setFitWidth(48);
                if (piece != null)
                    pieceImageView.setImage(piece.getPieceImage());

                gameState.setPieceImageView(pieceImageView, currentPosition);

                Button tile = new Button();
                tile.setPrefHeight(48);
                tile.setPrefWidth(48);
                tile.setPadding(new Insets(16));
                tile.setGraphic(gameState.getPieceImageView(currentPosition));
                tile.setOnMouseEntered(event -> {
                    if (piece == null) {
                        tile.setCursor(Cursor.DEFAULT);
                    }else
                        tile.setCursor(piece.getPlayer() == gameState.getCurrentPlayer() ? Cursor.HAND : Cursor.DEFAULT);
                });
                tile.setOnMouseClicked(this);

                String tileBackground = (i + j) % 2 == 0 ? "light-tile" : "dark-tile";
                tile.getStyleClass().add(tileBackground);
                boardGridView.add(tile, j, i, 1, 1);
            }
        }
    }

    private void toggleCurrentPlayer() {
        gameState.toggleCurrentPlayer();
        turnLabel.setText("Vez de " + gameState.getCurrentPlayer());
    }

    private void selectTile(Button clickedButton){
        if (lastClickedButton != null)
            lastClickedButton.getStyleClass().remove("selected");

        lastClickedButton = clickedButton;

        if (clickedButton != null)
            clickedButton.getStyleClass().add("selected");
    }

    private void highlightCheckWarning(Player player, boolean highlight){
        BoardPosition king = BoardUtils.getPlayerKing(player);
        if (king == null)
            return;

        Button threatenedKingButton = (Button) gameState.getPieceImageView(king).getParent();

        if (highlight)
            threatenedKingButton.getStyleClass().add("check");
        else
            threatenedKingButton.getStyleClass().removeAll("check");
    }

    private BoardPosition getClickIndex(Node target) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++)
                if (gameState.getPieceImageView(i, j).equals(target))
                    return new BoardPosition(i, j);
        }

        return null;
    }

    private void completeTurn(MoveEvent moveEvent) {
        Piece piece = gameState.getPiece(moveEvent.getDestination());

        boolean whiteCheck = Validator.checkValidation(Player.WHITE);
        boolean blackCheck = Validator.checkValidation(Player.BLACK);

        highlightCheckWarning(Player.BLACK, whiteCheck);
        highlightCheckWarning(Player.WHITE, blackCheck);

        boolean checkmatte = false;

        if (whiteCheck || blackCheck)
            checkmatte = Validator.checkmatteValidation(piece);

        GameTurnLog gameTurnLog = new GameTurnLog(piece, moveEvent);
        gameTurnLog.writeToFile();
        gameState.getGameTurnsLog().add(gameTurnLog);

        toggleCurrentPlayer();
    }

    //On Tile Click
    @Override
    public void handle(MouseEvent event) {
        Button clickedButton = (Button) event.getSource();
        BoardPosition clickedPosition = getClickIndex(clickedButton.getGraphic());

        if (clickedPosition == null)
            return;

        if (sourcePosition == null) {
            Piece piece = gameState.getPiece(clickedPosition);
            if (piece == null || piece.getPlayer() != gameState.getCurrentPlayer())
                return;

            sourcePosition = clickedPosition;
            selectTile(clickedButton);
            BoardUtils.showPossiblePaths(piece);
        } else if (destinationPosition == null) {
            destinationPosition = clickedPosition;
            MoveEvent moveEvent = BoardUtils.move(sourcePosition, destinationPosition);
            if (moveEvent.isSuccess())
                completeTurn(moveEvent);

            BoardUtils.clearPaths();
            selectTile(null);
            sourcePosition = destinationPosition = null;
        }
    }
}