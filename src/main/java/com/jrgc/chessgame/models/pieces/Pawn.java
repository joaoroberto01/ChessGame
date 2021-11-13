package com.jrgc.chessgame.models.pieces;

import com.jrgc.chessgame.GameState;
import com.jrgc.chessgame.models.BoardPosition;
import com.jrgc.chessgame.models.GameTurnLog;
import com.jrgc.chessgame.models.MoveEvent;
import com.jrgc.chessgame.models.Player;

import java.util.List;

public class Pawn extends Piece {

    protected Pawn(Player player, BoardPosition boardPosition){
        super(player, PieceType.PAWN, boardPosition);
    }

    public Piece transform(PieceType pieceType){
        return copy(pieceType);
    }

    public boolean isThreatTo(BoardPosition boardPosition){
        for (BoardPosition possibleMove : getPossibleMoves()){
            if (possibleMove.equals(boardPosition)) {
                BoardPosition delta = getBoardPosition().delta(possibleMove);

                if (delta.column == 0)
                    continue;

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canMove(BoardPosition to) {
        GameState gameState = GameState.getInstance();

        BoardPosition from = getBoardPosition();
        Piece destinationPiece = gameState.getPieceAt(to);
        boolean playersDiff = destinationPiece != null && getPlayer() != destinationPiece.getPlayer();
        boolean verticalFree = true;

        if (cantPreventCheckmatte(to))
            return false;

        int range = getMoveCount() == 0 ? 2 : 1;

        BoardPosition delta = from.delta(to);
        if (getPlayer() == Player.BLACK)
            delta.line = -delta.line;

        BoardPosition target = new BoardPosition(from.line, from.column);
        for (int i = 0; i < delta.line; i++) {
            target.line += getPlayer() == Player.BLACK ? 1 : -1;

            Piece piece = gameState.getPieceAt(target);
            if (piece != null)
                verticalFree = false;
        }

        return (destinationPiece == null && verticalFree && delta.column == 0 && delta.line > 0 && delta.line <= range)
                || (playersDiff && Math.abs(delta.column) == 1 && delta.line == 1) || enPassantCapture(from, to) != null;
    }

    public Piece enPassantCapture(BoardPosition from, BoardPosition to) {
        List<GameTurnLog> gameTurnLogs = GameState.getGameTurnsLog();
        if (gameTurnLogs.isEmpty())
            return null;

        int lastIndex = gameTurnLogs.size() - 1;
        GameTurnLog lastTurn = gameTurnLogs.get(lastIndex);

        if (lastTurn.getPiece().getPieceType() != PieceType.PAWN)
            return null;

        MoveEvent lastMove = lastTurn.getMoveEvent();

        BoardPosition lastMoveDestination = lastMove.getDestination();
        BoardPosition lastMoveDelta = lastMove.getSource().delta(lastMoveDestination);

        BoardPosition delta = from.delta(to);
        if (getPlayer() == Player.BLACK)
            delta.line = -delta.line;

        boolean horizontalLastMove = Math.abs(lastMoveDelta.line) == 2 && lastMoveDelta.column == 0;
        boolean diagonalMove = Math.abs(delta.line) == Math.abs(delta.column) && delta.line == 1;
        boolean destinationIsBehind = to.column == lastMoveDestination.column && to.line != lastMoveDestination.line &&
                BoardPosition.isInRange(to.line, lastMoveDestination.line, getPlayer() == Player.WHITE ? 0 : 7);

        return horizontalLastMove && diagonalMove && destinationIsBehind ? lastTurn.getPiece() : null;
    }
}
