package com.jrgc.chessgame.models.pieces;

import com.jrgc.chessgame.models.BoardPosition;
import com.jrgc.chessgame.models.Player;

public class King extends Piece {
    protected King(Player player, BoardPosition boardPosition){
        super(player, PieceType.KING, boardPosition);
    }

    @Override
    public boolean canMove(BoardPosition to) {
        BoardPosition from = getBoardPosition();

        return Validator.crossValidation(this, 1, to)
                || Validator.lineValidation(this, 1, to);
    }
}
