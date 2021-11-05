package com.jrgc.chessgame.models.pieces;

import com.jrgc.chessgame.ChessApplication;
import com.jrgc.chessgame.models.BoardPosition;
import com.jrgc.chessgame.models.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

import static com.jrgc.chessgame.BoardUtils.BOARD_SIZE;

public abstract class Piece {

    public enum PieceType {
        PAWN, ROOK, KNIGHT, BISHOP, KING, QUEEN;

        public static PieceType get(int i, int j){
            PieceType[] pieces = new PieceType[]{ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK};
            if (i == 1 || i == 6)
                return PAWN;
            if (i == 0 || i == 7)
                return pieces[j];

            return null;
        }

        @Override
        public String toString() {
            return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
        }
    }

    private final Player player;
    private final PieceType pieceType;
    private BoardPosition boardPosition;
    private int moveCount = 0;

    protected Piece(Player player, PieceType pieceType, BoardPosition boardPosition){
        this.player = player;
        this.pieceType = pieceType;
        this.boardPosition = boardPosition;
    }

    public Player getPlayer() {
        return player;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void incrementMoveCount() {
        moveCount++;
    }

    public BoardPosition getBoardPosition() {
        return boardPosition;
    }

    public void setBoardPosition(int line, int column) {
        boardPosition.line = line;
        boardPosition.column = column;
    }

    public List<BoardPosition> getPossibleMoves(){
        final List<BoardPosition> possibleMoves = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++){
            for (int j = 0; j < BOARD_SIZE; j++){
                BoardPosition to = new BoardPosition(i, j);
                if (canMove(to))
                    possibleMoves.add(to);
            }
        }

        return possibleMoves;
    }

    public static Piece create(int i, int j){
        Player player = Player.get(i);
        PieceType pieceType = PieceType.get(i, j);

        if (player == null || pieceType == null)
            return null;

        BoardPosition boardPosition = new BoardPosition(i, j);

        return switch (pieceType){
            case PAWN -> new Pawn(player, boardPosition);
            case ROOK -> new Rook(player, boardPosition);
            case KNIGHT -> new Knight(player, boardPosition);
            case BISHOP -> new Bishop(player, boardPosition);
            case KING -> new King(player, boardPosition);
            case QUEEN -> new Queen(player, boardPosition);
        };
    }

    public Image getPieceImage() {
        String path = "images/" + getPieceType().toString().toLowerCase();
        path += getPlayer() == Player.WHITE ? "_white.png" : "_black.png";

        return new Image(ChessApplication.class.getResourceAsStream(path));
    }

    @Override
    public String toString() {
        return player + " " + pieceType + "{boardPosition=" + boardPosition.toFormattedString() + "}";
    }

    public abstract boolean canMove(BoardPosition to);
}
