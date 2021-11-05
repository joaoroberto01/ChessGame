package com.jrgc.chessgame.models.pieces;

import com.jrgc.chessgame.controllers.GameStateController;
import com.jrgc.chessgame.models.BoardPosition;
import com.jrgc.chessgame.models.Player;

public class Knight extends Piece {
    protected Knight(Player player, BoardPosition boardPosition){
        super(player, PieceType.KNIGHT, boardPosition);
    }

    @Override
    public boolean canMove(BoardPosition to) {
        GameStateController gameState = GameStateController.getInstance(false);

        Piece destinationPiece = gameState.getPiece(to);
        boolean playersDiff = destinationPiece != null && getPlayer() != destinationPiece.getPlayer();

        BoardPosition deltaPosition = getBoardPosition().deltaAbs(to);

        if (deltaPosition.line == 0 || deltaPosition.line > 2 || deltaPosition.column == 0 || deltaPosition.column > 2)
            return false;

        int deltaDiff = Math.abs(deltaPosition.line - deltaPosition.column);
        return deltaDiff == 1 && (destinationPiece == null || playersDiff);
    }
}
