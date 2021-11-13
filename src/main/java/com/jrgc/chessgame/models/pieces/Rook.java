package com.jrgc.chessgame.models.pieces;

import com.jrgc.chessgame.models.BoardPosition;
import com.jrgc.chessgame.models.Player;

public class Rook extends Piece {
    protected Rook(Player player, BoardPosition boardPosition){
        super(player, PieceType.ROOK, boardPosition);
    }

    @Override
    public boolean canMove(BoardPosition to) {
        if (cantPreventCheckmatte(to))
            return false;

        return Validator.lineValidation(this,7, to);
    }
}
