package com.jrgc.chessgame.models.pieces;

import com.jrgc.chessgame.models.game.BoardPosition;
import com.jrgc.chessgame.models.game.Player;
import com.jrgc.chessgame.utils.Validator;

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

    public static String getRule(){
        return """
        A torre se movimenta nas direções horizontais e verticais, quantas casas quiser, desde que estejam desobstruídas.""";
    }
}
