package com.jrgc.chessgame.models.game.log;

import com.jrgc.chessgame.models.game.MoveEvent;
import com.jrgc.chessgame.models.pieces.Pawn;
import com.jrgc.chessgame.models.pieces.Piece;

public class GameTurn {
    private final Piece piece;
    private final MoveEvent moveEvent;

    public GameTurn(Piece piece, MoveEvent moveEvent) {
        this.piece = piece;
        this.moveEvent = moveEvent;
    }

    public Piece getPiece(){
        return piece;
    }

    public MoveEvent getMoveEvent() {
        return moveEvent;
    }

    @Override
    public String toString() {
        if (moveEvent.getCastlingType() == MoveEvent.CastlingType.LONG)
            return "O-O-O";

        if (moveEvent.getCastlingType() == MoveEvent.CastlingType.SHORT)
            return "O-O";

        String position = moveEvent.getDestination().toFormattedString();
        String pieceLetter = piece.getPieceType().toLetter();
        String moveSuffix = "";

        if (moveEvent.getAmbiguity() == MoveEvent.AmbiguitityType.ROW) {
            if (!(piece instanceof Pawn))
                pieceLetter += moveEvent.getSource().formattedColumn();
        }else if (moveEvent.getAmbiguity() == MoveEvent.AmbiguitityType.COLUMN)
            pieceLetter += moveEvent.getSource().formattedRow();

        if (moveEvent.hasCaptured()){
            if (piece instanceof Pawn)
                pieceLetter += getMoveEvent().getSource().formattedColumn();
            pieceLetter += "x";
        }

        if (moveEvent.getPromotedPiece() != null)
            moveSuffix = moveEvent.getPromotedPiece().toLetter();

        if (moveEvent.isCheckmatte())
            moveSuffix += "#";
        else if (moveEvent.isCheck())
            moveSuffix += "+";


        return String.format("%s%s%s", pieceLetter, position, moveSuffix);
    }


}