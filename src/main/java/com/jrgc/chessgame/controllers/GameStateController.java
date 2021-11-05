package com.jrgc.chessgame.controllers;

import com.jrgc.chessgame.models.BoardPosition;
import com.jrgc.chessgame.models.GameTurnLog;
import com.jrgc.chessgame.models.Player;
import com.jrgc.chessgame.models.pieces.Piece;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

import static com.jrgc.chessgame.BoardUtils.BOARD_SIZE;

public class GameStateController {
    private static GameStateController gameState;

    private final Piece[][] board = new Piece[BOARD_SIZE][BOARD_SIZE];
    private final ImageView[][] boardImageViews = new ImageView[BOARD_SIZE][BOARD_SIZE];

    private final List<GameTurnLog> gameTurnsLog = new ArrayList<>();

    private Player currentPlayer = Player.WHITE;
    private boolean whiteCheck = false, blackCheck = false;

    private final List<Piece> whitePieces = new ArrayList<>();
    private final List<Piece> blackPieces = new ArrayList<>();

    private GameStateController(){
        GameTurnLog.clearLog();
    };

    public static GameStateController getInstance(boolean reset){
        if (gameState == null || reset)
            gameState = new GameStateController();
        return gameState;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void toggleCurrentPlayer(){
        currentPlayer = Player.getOpponent(currentPlayer);
    }

    public boolean isWhiteCheck(){
        return whiteCheck;
    }

    public boolean isBlackCheck(){
        return blackCheck;
    }

    public void setWhiteCheck(boolean whiteCheck) {
        this.whiteCheck = whiteCheck;
    }

    public void setBlackCheck(boolean blackCheck) {
        this.blackCheck = blackCheck;
    }

    public List<GameTurnLog> getGameTurnsLog() {
        return gameTurnsLog;
    }

    public List<Piece> getPlayerPieces(Player player) {
        return player == Player.WHITE ? whitePieces : blackPieces;
    }

    public Piece getPiece(BoardPosition boardPosition) {
        return board[boardPosition.line][boardPosition.column];
    }

    public Piece getPiece(int i, int j){
        return getPiece(new BoardPosition(i, j));
    }

    public void setPiece(Piece piece, BoardPosition boardPosition){
        board[boardPosition.line][boardPosition.column] = piece;
    }

    public ImageView getPieceImageView(BoardPosition boardPosition){
        return boardImageViews[boardPosition.line][boardPosition.column];
    }

    public ImageView getPieceImageView(int i, int j){
        return getPieceImageView(new BoardPosition(i, j));
    }

    public void setPieceImageView(ImageView pieceImageView, BoardPosition boardPosition){
        boardImageViews[boardPosition.line][boardPosition.column] = pieceImageView;
    }
}
