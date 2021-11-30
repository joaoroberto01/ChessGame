package com.jrgc.chessgame.utils;

import com.jrgc.chessgame.GameState;
import com.jrgc.chessgame.models.game.BoardPosition;
import com.jrgc.chessgame.models.game.MoveEvent;
import com.jrgc.chessgame.models.game.Player;
import com.jrgc.chessgame.models.pieces.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;

public class BoardUtils {
    public static final int BOARD_SIZE = 8;

    public static King getPlayerKing(Player player){
        GameState gameState = GameState.getInstance();

        for (Piece piece : gameState.getPlayerPieces(player)){
            if (piece.getPieceType() == Piece.PieceType.KING)
                return (King) piece;
        }

        return null;
    }

    public static MoveEvent moveOnBoard(BoardPosition from, BoardPosition to){
        GameState gameState = GameState.getInstance();
        Piece sourcePiece = gameState.getPieceAt(from);

        MoveEvent moveEvent = new MoveEvent(from, to);

        if (sourcePiece == null || !sourcePiece.canMove(to) || Validator.checkmatteIfMove(sourcePiece, to))
            return moveEvent;

        gameState = GameState.getInstance();
        moveEvent.setSuccess(true);

        Player currentPlayer = sourcePiece.getPlayer();

        Image sourceImage = gameState.getPieceImageView(from).getImage();

        Piece destinationPiece = gameState.getPieceAt(to);
        moveEvent.setCapturedPiece(destinationPiece);

        if (sourcePiece instanceof King){
            MoveEvent.CastlingType castlingType = ((King) sourcePiece).castlingMove(to);
            moveEvent.setCastlingType(castlingType);

            if (castlingType != MoveEvent.CastlingType.NONE) {
                int playerRow = currentPlayer == Player.WHITE ? 7 : 0;
                int rookColumn = castlingType == MoveEvent.CastlingType.SHORT ? 7 : 0;

                Piece rook = gameState.getPieceAt(playerRow, rookColumn);

                if (rook != null) {
                    gameState.setPieceAt((Piece) null, rook.getBoardPosition());
                    gameState.getPieceImageView(rook.getBoardPosition()).setImage(null);

                    int behind = 1;
                    if (castlingType == MoveEvent.CastlingType.SHORT)
                        behind = -behind;

                    rook.getBoardPosition().column = to.column + behind;
                    gameState.setPieceAt(rook, rook.getBoardPosition());
                    gameState.getPieceImageView(rook.getBoardPosition()).setImage(rook.getPieceImage());
                }
            }
        }

        for (Piece otherPiece : gameState.getPlayerPieces(currentPlayer)){
            if (sourcePiece.getPieceType() == otherPiece.getPieceType() && !sourcePiece.equals(otherPiece) &&
                    otherPiece.canMove(moveEvent.getDestination())) {
                BoardPosition otherPiecePosition = otherPiece.getBoardPosition();
                BoardPosition piecePosition = moveEvent.getSource();

                moveEvent.setAmbiguitityType(piecePosition.column == otherPiecePosition.column ?
                        MoveEvent.AmbiguitityType.COLUMN : MoveEvent.AmbiguitityType.ROW);

                break;
            }
        }

        gameState.setPieceAt(sourcePiece, to);
        gameState.getPieceImageView(to).setImage(sourceImage);
        sourcePiece.setBoardPosition(to.row, to.column);

        gameState.setPieceAt((Piece) null, from);
        gameState.getPieceImageView(from).setImage(null);

        gameState.getPieceImageView(to).setImage(sourceImage);
        gameState.getPieceImageView(from).setImage(null);

        if (sourcePiece instanceof Pawn){
            Piece enPassantPiece = ((Pawn) sourcePiece).enPassantCapture(from, to);
            if (enPassantPiece != null) {
                moveEvent.setCapturedPiece(enPassantPiece);
                gameState.setPieceAt((Piece) null, enPassantPiece.getBoardPosition());
                gameState.getPieceImageView(enPassantPiece.getBoardPosition()).setImage(null);
            }
            moveEvent.setPawnPromoted(to.row == 0 || to.row == 7);
        }

        if (moveEvent.hasCaptured()) {
            Player player = sourcePiece.getPlayer();
            gameState.getPlayerPieces(Player.getOpponent(player)).remove(moveEvent.getCapturedPiece());
        }

        sourcePiece.incrementMoveCount();

        updateAllPossibleMoves();

        return moveEvent;
    }

    public static void updateAllPossibleMoves(){
        GameState gameState = GameState.getInstance();

        for (Piece white : gameState.getPlayerPieces(Player.WHITE))
            white.setPossibleMoves();

        for (Piece black : gameState.getPlayerPieces(Player.BLACK))
            black.setPossibleMoves();
    }

    public static void showPossiblePaths(Piece piece) {
        GameState gameState = GameState.getInstance();
        for (BoardPosition possiblePosition : piece.getPossibleMoves()){
            if (!Validator.checkmatteIfMove(piece, possiblePosition)) {
                Button button = (Button) gameState.getPieceImageView(possiblePosition).getParent();
                button.getStyleClass().add("possible-path");
            }
        }
    }

    public static void clearPaths(){
        GameState gameState = GameState.getInstance();

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Button button = (Button) gameState.getPieceImageView(i, j).getParent();
                button.getStyleClass().removeAll("possible-path");
            }
        }
    }
}
