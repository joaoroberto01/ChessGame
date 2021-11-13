package com.jrgc.chessgame.controllers;

import com.jrgc.chessgame.ChessApplication;
import com.jrgc.chessgame.SoundAlert;
import com.jrgc.chessgame.models.*;
import com.jrgc.chessgame.utils.BoardUtils;
import com.jrgc.chessgame.GameState;
import com.jrgc.chessgame.utils.SceneManager;
import com.jrgc.chessgame.interfaces.PawnPromotionListener;
import com.jrgc.chessgame.models.pieces.King;
import com.jrgc.chessgame.models.pieces.Pawn;
import com.jrgc.chessgame.models.pieces.Piece;
import com.jrgc.chessgame.models.pieces.Validator;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import static com.jrgc.chessgame.utils.BoardUtils.BOARD_SIZE;
import static com.jrgc.chessgame.utils.BoardUtils.updateAllPossibleMoves;

public class GameController implements EventHandler<MouseEvent> {

    @FXML
    private Label turnLabel;

    @FXML
    public GridPane boardGridView;

    private BoardPosition sourcePosition = null, destinationPosition = null;
    private Button lastClickedButton;
    private GameState gameState;

    public void initialize() {
        gameState = GameState.getInstance(true);
        setupGrid();
        toggleCurrentPlayer();
    }

    @FXML
    private void checkCase() {
        select(source(6,5));
        select(source(4,5));

        select(source(1,1));
        select(source(2,1));

        select(source(6,6));
        select(source(4,6));

        select(source(1,4));
        select(source(2,4));

        select(source(6,4));
        select(source(4,4));

        select(source(6,0));
        select(source(5,0));

        select(source(0,3));
        select(source(4,7));
        //clicked(source(7,4));
    }

    @FXML
    private void checkmatteCase() {
        select(source(6,4));
        select(source(4,4));

        select(source(1,4));
        select(source(2,4));

        select(source(7,5));
        select(source(2,0));

        select(source(1,1));
        select(source(2,1));

        select(source(6,5));
        select(source(4,5));

        select(source(0,2));
        select(source(2,0));

        select(source(6,6));
        select(source(4,6));

        select(source(0,3));
        select(source(4,7));

//        clicked(source(6,0));
//        clicked(source(5,0));
//
//        clicked(source(0,3));
//        clicked(source(4,7));
        //clicked(source(7,4));
    }

    private Button source(int i, int j){
        return (Button) gameState.getPieceImageView(i, j).getParent();
    }

    public void onResetClick() {
        initialize();
    }

    private double getBoardCenterX(Stage stage) {
        return stage.getX() + boardGridView.getWidth() / 3;
    }

    private double getBoardCenterY(Stage stage) {
        return stage.getY() + boardGridView.getHeight() / 2 - 20;
    }

    private void setupGrid() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                BoardPosition currentPosition = new BoardPosition(i, j);
                Piece piece = Piece.create(i, j);
                gameState.setPieceAt(piece, currentPosition);

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
                    tile.setCursor(piece == null || piece.getPlayer() != gameState.getCurrentPlayer() ? Cursor.DEFAULT : Cursor.HAND);
                });
                tile.setOnMouseClicked(this);

                String tileBackground = (i + j) % 2 == 0 ? "light-tile" : "dark-tile";
                tile.getStyleClass().add(tileBackground);
                boardGridView.add(tile, j, i, 1, 1);
            }
        }

        for (Piece whitePiece : gameState.getPlayerPieces(Player.WHITE))
            whitePiece.setPossibleMoves();

        for (Piece blackPiece : gameState.getPlayerPieces(Player.BLACK))
            blackPiece.setPossibleMoves();

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

    private void deselectCheckWarning(){
        for (int i = 0; i < BOARD_SIZE; i++){
            for (int j = 0; j < BOARD_SIZE; j++){
                Button button = (Button) gameState.getPieceImageView(i, j).getParent();
                button.getStyleClass().removeAll("check");
            }
        }
    }

    private void highlightCheckWarning(Player player, boolean highlight){
        King king = BoardUtils.getPlayerKing(player);
        if (king == null)
            return;

        Button threatenedKingButton = (Button) gameState.getPieceImageView(king.getBoardPosition()).getParent();

        if (highlight && !threatenedKingButton.getStyleClass().contains("check"))
            threatenedKingButton.getStyleClass().add("check");
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
        Piece movedPiece = gameState.getPieceAt(moveEvent.getDestination());

        if (moveEvent.isPawnPromoted())
            openPawnPromotionDialog(movedPiece);

        boolean whiteCheck = Validator.checkValidation(Player.WHITE);
        boolean blackCheck = Validator.checkValidation(Player.BLACK);

        gameState.setCheck(Player.WHITE, whiteCheck);
        gameState.setCheck(Player.BLACK, blackCheck);

        deselectCheckWarning();

        if (gameState.isCheck())
            gameState.setCheckmatte(Validator.checkmatteValidation(movedPiece));

        gameState.setDraw(Validator.draw());
        gameState.updateGameStatus();

        GameState.saveTurnLog(movedPiece, moveEvent);

        Alert alert;
        switch (gameState.getGameStatus()) {
            case CHECKMATTE -> {
                playSound(SoundAlert.CHECKMATTE);
                turnLabel.setText("CHECKMATTE");
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Parabéns, " + (whiteCheck ? "Branco" : "Preto"));
                alert.setContentText("CHEQUEMATTE");
                alert.show();
                return;
            }
            case DRAW -> {
                playSound(SoundAlert.DRAW);
                turnLabel.setText("AFFOGATION DRAW");
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Its a fucking draw");
                alert.setContentText("AFFOGATION DRAWWW");
                alert.show();
                return;
            }
            case CHECK -> {
                playSound(SoundAlert.CHECK);
                highlightCheckWarning(Player.BLACK, whiteCheck);
                highlightCheckWarning(Player.WHITE, blackCheck);
            }
            case RUNNING -> playSound(moveEvent.hasCaptured() ? SoundAlert.CAPTURE : SoundAlert.MOVE);
        }

        toggleCurrentPlayer();
        updateAllPossibleMoves();
    }

    private void openPawnPromotionDialog(Piece movedPiece) {
        Stage stage = (Stage) boardGridView.getScene().getWindow();
        stage.getScene().getRoot().setDisable(true);

        double x = getBoardCenterX(stage);
        double y = getBoardCenterY(stage);

        SceneManager.popUpPawnPromotion(stage, x, y, movedPiece.getPlayer(), new PawnPromotionListener() {
            @Override
            public void onPromotionPieceSelected(Piece.PieceType pieceType) {
                if (!(movedPiece instanceof Pawn))
                    return;

                Pawn pawn = (Pawn) movedPiece;
                Piece newPiece = pawn.transform(pieceType);

                gameState.setPieceAt(newPiece, movedPiece.getBoardPosition());
                gameState.getPlayerPieces(pawn.getPlayer()).replaceAll(piece -> piece.equals(pawn) ? newPiece : piece);
                gameState.getPieceImageView(pawn.getBoardPosition()).setImage(newPiece.getPieceImage());
                updateAllPossibleMoves();
                playSound(SoundAlert.PAWN_PROMOTION);
            }
        });

        stage.getScene().getRoot().setDisable(false);
    }

    private void playSound(SoundAlert soundAlert) {
        AudioClip audioClip = new AudioClip(ChessApplication.class.getResource(soundAlert.getUrl()).toString());
        audioClip.play();
    }

    //On Tile Click
    @Override
    public void handle(MouseEvent event) {
        select((Button) event.getSource());
    }

    private void select(Button clickedButton) {
        if (gameState.isCheckmatte())
            return;

        BoardPosition clickedPosition = getClickIndex(clickedButton.getGraphic());

        if (clickedPosition == null)
            return;

        if (sourcePosition == null) {
            Piece selectedPiece = gameState.getPieceAt(clickedPosition);
            if (selectedPiece == null || selectedPiece.getPlayer() != gameState.getCurrentPlayer())
                return;

            sourcePosition = clickedPosition;
            selectTile(clickedButton);
            selectedPiece.setPossibleMoves();
            BoardUtils.showPossiblePaths(selectedPiece);
        } else if (destinationPosition == null) {
            destinationPosition = clickedPosition;
            MoveEvent moveEvent = BoardUtils.moveOnBoard(sourcePosition, destinationPosition);
            if (moveEvent.isSuccess())
                completeTurn(moveEvent);
            else
                playSound(SoundAlert.INVALID);

            BoardUtils.clearPaths();
            selectTile(null);
            sourcePosition = destinationPosition = null;
        }
    }
}