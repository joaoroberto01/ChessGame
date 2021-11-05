package com.jrgc.chessgame.models.pieces;

import com.jrgc.chessgame.controllers.GameStateController;
import com.jrgc.chessgame.models.BoardPosition;
import com.jrgc.chessgame.models.Player;

public class Pawn extends Piece {

    protected Pawn(Player player, BoardPosition boardPosition){
        super(player, PieceType.PAWN, boardPosition);
    }

    @Override
    public boolean canMove(BoardPosition to) {
        GameStateController gameState = GameStateController.getInstance(false);

        BoardPosition from = getBoardPosition();
        Piece destinationPiece = gameState.getPiece(to);
        boolean playersDiff = destinationPiece != null && getPlayer() != destinationPiece.getPlayer();
        boolean verticalFree = true;

        int range = getMoveCount() == 0 ? 2 : 1;

        BoardPosition delta = from.delta(to);

        if (getPlayer() == Player.BLACK)
            delta.line = -delta.line;

        BoardPosition target = new BoardPosition(from.line, from.column);
        for (int i = 0; i < delta.line; i++) {
            target.line += getPlayer() == Player.BLACK ? 1 : -1;

            Piece piece = gameState.getPiece(target);
            if (piece != null)
                verticalFree = false;
        }

        return (destinationPiece == null && verticalFree && delta.column == 0 && delta.line > 0 && delta.line <= range)
                || (playersDiff && Math.abs(delta.column) == 1 && delta.line == 1);
    }
}
