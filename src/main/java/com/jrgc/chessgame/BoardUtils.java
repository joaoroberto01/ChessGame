package com.jrgc.chessgame;

import com.jrgc.chessgame.controllers.GameStateController;
import com.jrgc.chessgame.models.BoardPosition;
import com.jrgc.chessgame.models.MoveEvent;
import com.jrgc.chessgame.models.Player;
import com.jrgc.chessgame.models.pieces.Piece;
import javafx.scene.control.Button;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class BoardUtils {
    public static final int BOARD_SIZE = 8;


    public static BoardPosition getPlayerKing(Player player){
        GameStateController gameState = GameStateController.getInstance(false);

        for (int i = 0; i < BOARD_SIZE; i++){
            for (int j = 0; j < BOARD_SIZE; j++){
                Piece piece = gameState.getPiece(i, j);
                if (piece != null && piece.getPlayer() == player && piece.getPieceType() == Piece.PieceType.KING)
                    return new BoardPosition(i, j);
            }
        }

        return null;
    }

    public static MoveEvent move(BoardPosition from, BoardPosition to){
        GameStateController gameState = GameStateController.getInstance(false);
        Piece sourcePiece = gameState.getPiece(from);

        MoveEvent moveEvent = new MoveEvent(from, to);

        if (sourcePiece == null || !sourcePiece.canMove(to)){
            System.out.println("can move: false");
            return moveEvent;
        }
        System.out.println("can move: true");
        moveEvent.setSuccess(true);

        Image sourceImage = gameState.getPieceImageView(from).getImage();

        Piece destinationPiece = gameState.getPiece(to);
        moveEvent.setCaptured(destinationPiece != null);

        if (moveEvent.hasCaptured()) {
            Player player = sourcePiece.getPlayer();
            gameState.getPlayerPieces(Player.getOpponent(player)).remove(destinationPiece);

            System.out.println(sourcePiece.getPieceType() + " " + player + " Captured " + destinationPiece);
        }

        gameState.setPiece(sourcePiece, to);
        gameState.getPieceImageView(to).setImage(sourceImage);
        sourcePiece.setBoardPosition(to.line, to.column);
        sourcePiece.incrementMoveCount();

        gameState.setPiece(null, from);
        gameState.getPieceImageView(from).setImage(null);

        return moveEvent;
    }

    public static void showPossiblePaths(Piece piece) {
        GameStateController gameState = GameStateController.getInstance(false);
        for (int i = 0; i < BOARD_SIZE; i++){
            for (int j = 0; j < BOARD_SIZE; j++){
                BoardPosition to = new BoardPosition(i, j);
                if (piece.canMove(to)) {
                    Button button = (Button) gameState.getPieceImageView(i, j).getParent();
                    button.getStyleClass().add("possible-path");
                }
            }
        }
    }

    public static void clearPaths(){
        GameStateController gameState = GameStateController.getInstance(false);
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Button button = (Button) gameState.getPieceImageView(i, j).getParent();
                button.getStyleClass().remove("possible-path");
            }
        }
    }
}
