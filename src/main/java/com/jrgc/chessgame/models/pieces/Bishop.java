package com.jrgc.chessgame.models.pieces;

import com.jrgc.chessgame.models.game.BoardPosition;
import com.jrgc.chessgame.models.game.Player;
import com.jrgc.chessgame.utils.Validator;

public class Bishop extends Piece {
    protected Bishop(Player player, BoardPosition boardPosition){
        super(player, PieceType.BISHOP, boardPosition);
    }

    @Override
    public boolean canMove(BoardPosition to) {
        if (cantPreventCheckmatte(to))
            return false;

        return Validator.crossValidation(this, 7, to);
    }

    public static String getRule(){
        return """
                O bispo se movimenta nas direções diagonais, quantas casas quiser, desde que estejam desobstruídas.""";
    }
}
