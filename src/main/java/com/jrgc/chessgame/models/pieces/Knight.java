package com.jrgc.chessgame.models.pieces;

import com.jrgc.chessgame.GameState;
import com.jrgc.chessgame.models.BoardPosition;
import com.jrgc.chessgame.models.Player;

public class Knight extends Piece {
    protected Knight(Player player, BoardPosition boardPosition){
        super(player, PieceType.KNIGHT, boardPosition);
    }

    @Override
    public boolean canMove(BoardPosition to) {
        GameState gameState = GameState.getInstance();

        Piece destinationPiece = gameState.getPieceAt(to);
        boolean playersDiff = destinationPiece != null && getPlayer() != destinationPiece.getPlayer();

        if (cantPreventCheckmatte(to))
            return false;

        BoardPosition deltaPosition = getBoardPosition().deltaAbs(to);

        if (deltaPosition.line == 0 || deltaPosition.line > 2 || deltaPosition.column == 0 || deltaPosition.column > 2)
            return false;

        int deltaDiff = Math.abs(deltaPosition.line - deltaPosition.column);
        return deltaDiff == 1 && (destinationPiece == null || playersDiff);
    }
}
