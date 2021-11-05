package com.jrgc.chessgame.models.pieces;

import com.jrgc.chessgame.models.BoardPosition;
import com.jrgc.chessgame.models.Player;

public class Bishop extends Piece {
    protected Bishop(Player player, BoardPosition boardPosition){
        super(player, PieceType.BISHOP, boardPosition);
    }

    @Override
    public boolean canMove(BoardPosition to) {
        return Validator.crossValidation(this, 7, to);
    }
}
