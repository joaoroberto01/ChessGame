package com.jrgc.chessgame.models.pieces;

import com.jrgc.chessgame.models.game.BoardPosition;
import com.jrgc.chessgame.models.game.Player;
import com.jrgc.chessgame.utils.Validator;

public class Queen extends Piece {
    protected Queen(Player player, BoardPosition boardPosition){
        super(player, PieceType.QUEEN, boardPosition);
    }

    @Override
    public boolean canMove(BoardPosition to) {
        if (cantPreventCheckmatte(to))
            return false;

        return Validator.crossValidation(this, 7, to)
                || Validator.lineValidation(this, 7, to);
    }

    public static String getRule(){
        return """
        A rainha se movimenta em todas as direções (diagonal, vertical ou horizontal), quantas casas queira,
         desde que estejam desobstruídas.""";
    }
}
