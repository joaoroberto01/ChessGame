package com.jrgc.chessgame.models.pieces;

import com.jrgc.chessgame.models.BoardPosition;
import com.jrgc.chessgame.models.Player;

public class Queen extends Piece {
    protected Queen(Player player, BoardPosition boardPosition){
        super(player, PieceType.QUEEN, boardPosition);
    }

    @Override
    public boolean canMove(BoardPosition to) {
        return Validator.crossValidation(this, 7, to)
                || Validator.lineValidation(this, 7, to);
    }
}
