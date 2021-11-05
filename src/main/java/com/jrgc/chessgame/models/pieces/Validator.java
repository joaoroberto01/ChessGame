package com.jrgc.chessgame.models.pieces;

import com.jrgc.chessgame.BoardUtils;
import com.jrgc.chessgame.controllers.GameStateController;
import com.jrgc.chessgame.models.BoardPosition;
import com.jrgc.chessgame.models.Player;

import java.util.List;

public class Validator {
    protected static boolean crossValidation(Piece currentPiece, int range, BoardPosition to){
        GameStateController gameState = GameStateController.getInstance(false);

        Piece destinationPiece = gameState.getPiece(to);
        boolean playersDiff = destinationPiece != null && currentPiece.getPlayer() != destinationPiece.getPlayer();

        BoardPosition from = currentPiece.getBoardPosition();

        BoardPosition delta = from.deltaAbs(to);

        if (delta.column == delta.line) {
            int stepColumn = - Integer.compare(from.column, to.column);
            int stepLine = - Integer.compare(from.line, to.line);

            BoardPosition target = new BoardPosition(from.line, from.column);
            for (int i = 0; i < range; i++) {
                target.line += stepLine;
                target.column += stepColumn;

                Piece piece = gameState.getPiece(target);
                if (target.equals(to)) {
                    return piece == null || playersDiff;
                }else if (piece != null)
                    break;

                if (target.line == 0 || target.column == 0)
                    break;
            }
        }

        return false;
    }

    protected static boolean lineValidation(Piece currentPiece, int range, BoardPosition to){
        GameStateController gameState = GameStateController.getInstance(false);

        Piece destinationPiece = gameState.getPiece(to);
        boolean playersDiff = destinationPiece != null && currentPiece.getPlayer() != destinationPiece.getPlayer();

        BoardPosition from = currentPiece.getBoardPosition();
        BoardPosition delta = from.deltaAbs(to);

        if (delta.column == 0 || delta.line == 0) {
            int stepColumn = - Integer.compare(from.column, to.column);
            int stepLine = - Integer.compare(from.line, to.line);

            BoardPosition target = new BoardPosition(from.line, from.column);
            for (int i = 0; i < range; i++) {
                target.line += stepLine;
                target.column += stepColumn;

                Piece piece = gameState.getPiece(target);
                if (target.equals(to))
                    return piece == null || playersDiff;
                else if (piece != null)
                    break;

                if ((target.line == 0 && stepLine != 0) || (target.column == 0 && stepColumn != 0))
                    break;
            }
        }

        return false;
    }

    public static boolean checkValidation(Player player){
        GameStateController gameState = GameStateController.getInstance(false);

        Player opponent = Player.getOpponent(player);
        BoardPosition kingPosition = BoardUtils.getPlayerKing(opponent);

        List<Piece> pieces = gameState.getPlayerPieces(player);

        boolean check = false;
        for (Piece piece : pieces)
            check = check || piece.getPossibleMoves().contains(kingPosition);

        return check;
    }

    public static boolean checkmatteValidation(Piece attackingPiece){
        GameStateController gameState = GameStateController.getInstance(false);

        Player player = attackingPiece.getPlayer();
        Player opponent = Player.getOpponent(player);
        BoardPosition kingPosition = BoardUtils.getPlayerKing(opponent);

        if (kingPosition == null)
            return false;

        List<BoardPosition> kingMoves = gameState.getPiece(kingPosition).getPossibleMoves();

        System.out.println("KingMoves " + kingMoves);

        List<Piece> pieces = gameState.getPlayerPieces(player);

        return false;
    }
}
